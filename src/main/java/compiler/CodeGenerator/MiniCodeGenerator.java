package compiler.CodeGenerator;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * The AST of a small programming language. A program consists of a single
 * procedure. The procedure can contain two kinds of statements:
 *      print(variable)
 *   or
 *      variable = value
 */

class Program {
    String name;                // name of the program
    Procedure procedure;        // the procedure
}

class Procedure {
    String name;                // name of procedure
    String[] variables;         // list of local variables
    Statement[] statements;     // list of statements
}

class Statement {
}

class PrintStatement extends Statement {
    String name;                // name of variable to print
}

class AssignmentStatement extends Statement {
    String name;                // name of variable
    int value;                  // value to assign to the variable
}



public class MiniCodeGenerator {

    // Generates code for an assignment statement of the form
    //     variable = value
    private static void generateCode(MethodVisitor mw, HashMap<String,Integer> variableMap, AssignmentStatement assignmentStatement) {
        // I am very lazy here and I just use the "ldc" instruction to push the value
        // onto the stack.
        // The correct way would be to use iconst_X, bipush, or sipush if the value is small.
        mw.visitLdcInsn(assignmentStatement.value);

        // Store the value in the variable
        var slot = variableMap.get(assignmentStatement.name);
        mw.visitVarInsn(Opcodes.ISTORE, slot);
    }

    // Generates code for a print statement of the form
    //     print(variable)
    private static void generateCode(MethodVisitor mw, HashMap<String,Integer> variableMap, PrintStatement printStatement) {
        // System.out
        mw.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // value of the variable
        var slot = variableMap.get(printStatement.name);
        mw.visitVarInsn(Opcodes.ILOAD, slot);

        // call println(int) of System.out
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
    }

    // Generates code for a procedure of the form
    //     static void theNameOfTheProcedure() {
    //         ...
    //     }
    private static void generateCode(ClassWriter cw, Procedure procedure) {
        // We map the variables to the variable slots in a simple way:
        //    Variable 0 is the first local variable of the procedure
        //    Variable 1 is the second local variable of the procedure
        //    etc.
        var variableMap = new HashMap<String, Integer>();
        for(var i=0; i<procedure.variables.length; i++) {
            variableMap.put(procedure.variables[i], i);
        }

        // create the method for the procedure
        var mw = cw.visitMethod(Opcodes.ACC_STATIC, procedure.name, "()V", null, null);
        mw.visitCode();

        // generate code for the statements
        for(var statement : procedure.statements) {
            // this is not very object oriented :(
            if(statement instanceof AssignmentStatement assignmentStatement) {
                generateCode(mw, variableMap, assignmentStatement);
            }
            else if(statement instanceof PrintStatement printStatement) {
                generateCode(mw, variableMap, printStatement);
            }
            else {
                throw new RuntimeException("Oops");
            }
        }

        // end of the method
        mw.visitInsn(Opcodes.RETURN);
        mw.visitEnd();
        mw.visitMaxs(-1, -1);
    }

    // Creates a class with a main method that calls the procedure of the program
    //
    //   class TheNameOfProgram {
    //      public static void main(String[] args) {
    //           theNameOfTheProcedure();
    //      }
    //
    //      static void theNameOfTheProcedure() {
    //          ...
    //      }
    //   }
    private static void generateCode(Program program) {
        // create class "TestProgram"
        var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, program.name, null, "java/lang/Object", null);

        // create main method
        var mw = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mw.visitCode();
        // call the procedure of the program
        mw.visitMethodInsn(Opcodes.INVOKESTATIC, program.name, program.procedure.name, "()V", false);
        mw.visitInsn(Opcodes.RETURN);
        mw.visitEnd();
        mw.visitMaxs(-1, -1);

        // generate the method for the procedure of the program
        generateCode(cw, program.procedure);

        // write class file
        cw.visitEnd();
        var bytes = cw.toByteArray();
        try(var outFile = new FileOutputStream(program.name+".class")) {
            outFile.write(bytes);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // a simple example program
        var statement1 = new AssignmentStatement();
        statement1.name = "a";
        statement1.value = 12;

        var statement2 = new AssignmentStatement();
        statement2.name = "b";
        statement2.value = 3;

        var statement3 = new PrintStatement();
        statement3.name = "b";

        var statement4 = new PrintStatement();
        statement4.name = "a";

        var procedure = new Procedure();
        procedure.name = "myProcedure";
        procedure.variables = new String[]{"a", "b"};
        procedure.statements = new Statement[]{ statement1, statement2, statement3, statement4};

        var exampleProgram = new Program();
        exampleProgram.name = "TestProgram";
        exampleProgram.procedure = procedure;

        // let's compile it
        generateCode(exampleProgram);

        // You can run the generated program with
        //     java TheNameOfTheProgram
        //
        // You can see the content of the class file with
        //     javap -v TheNameOfTheProgram
    }
}