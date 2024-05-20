import com.sun.tools.javac.Main;
import compiler.CodeGenerator.CodeGenerator;
import compiler.Lexer.Lexer;
import compiler.SemanticAnalysis.Errors.TypeError;
import org.junit.Test;
import compiler.Parser.*;
import compiler.SemanticAnalysis.*;
import compiler.SemanticAnalysis.Errors.*;
import compiler.*;

import java.io.*;
import java.util.Stack;

import static org.junit.Assert.*;
import org.junit.*;

//import Main;

public class TestCodeGenerator {
    @Before
    public void setUp() {

    }
    private static void buildCompiler() throws Exception {
        //Process process = Runtime.getRuntime().exec("./gradlew build -x test");
        //process.waitFor();
    }
    private static Stack<Reader> getStdLib() {
        // Getting all the file from the stdlib folder with .pedro extension
        File folder = new File("src/main/java/compiler/std");
        File[] listOfFiles = folder.listFiles();
        Stack<Reader> stdLibs = new Stack<>();
        for (File file : listOfFiles) {
            if (file.getName().equals("Utils.pedro")) {
                continue;
            }
            if (file.isFile() && file.getName().endsWith(".pedro")) {
                try {
                    stdLibs.push(new FileReader(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            stdLibs.push(new FileReader(new File("src/main/java/compiler/std/Utils.pedro")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stdLibs;
    }

    private static int generateCode(String input) throws Exception {
        Stack<Reader> std = getStdLib();
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader,std);
        Parser parser = new Parser(lexer, "test/import/");
        SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser);
        CodeGenerator codeGenerator = new CodeGenerator("test/build/Test.class");
        codeGenerator.generateCode(parser.getAST());
        return 0;
    }

    private static String runMain() throws Exception {
        try {
            Process process = Runtime.getRuntime().exec("java -cp ./test/build/ Test");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    ////////////////// Basic tests //////////////////
    @Test
    public void testSimpleAssignment() throws Exception {
        String input =
            """
            int x = 5;
            write(x);
            """;
        generateCode(input);
        assertEquals("5\n", runMain());
    }
    @Test
    public void testSimpleCalculation() throws Exception {
        String input =
            """
            int x = 5;
            int y = 3;
            write(x + y);
            """;
        generateCode(input);
        assertEquals("8\n", runMain());
    }
    @Test
    public void testSimpleIf() throws Exception {
        String input =
            """
            int x = 5;
            if (x == 5) {
                write(1);
            } else {
                write(0);
            }
            """;
        generateCode(input);
        assertEquals("1\n", runMain());
    }
    @Test
    public void testSimpleWhile() throws Exception {
        String input =
            """
            int x = 5;
            while (x > 0) {
                writeln(x);
                x = x - 1;
            }
            """;
        generateCode(input);
        assertEquals("5\n4\n3\n2\n1\n", runMain());
    }
    @Test
    public void testSimpleProcedure() throws Exception {
        String input =
            """
            def int giveMeFive() {
                return 5;
            }
            write(giveMeFive());
            """;
        generateCode(input);
        assertEquals("5\n", runMain());
    }
    @Test
    public void testSimpleProcedureWithParams() throws Exception {
        String input =
            """
            def int add(int x, int y) {
                return x + y;
            }
            write(add(8, 2));
            """;
        generateCode(input);
        assertEquals("10\n", runMain());
    }
    @Test
    public void testSimpleProcedureWithParamsAndAssignment() throws Exception {
        String input =
            """
            def int add(int x, int y) {
                return x + y;
            }
            int x = 10;
            int y = 2;
            write(add(x, y));
            """;
        generateCode(input);
        assertEquals("12\n", runMain());
    }
    @Test
    public void testSimpleCast() throws Exception {
        String input =
            """
            int x = 5;
            float y = 3.0;
            write(x + y);
            """;
        generateCode(input);
        assertEquals("8.0\n", runMain());
    }
    @Test
    public void testConcatenation() throws Exception {
        String input =
            """
            string x = "Hello";
            string y = "World";
            write(x + y);
            """;
        generateCode(input);
        assertEquals("HelloWorld\n", runMain());
    }
    @Test
    public void testSimpleArray() throws Exception {
        String input =
            """
            int[] x = int[3];
            x[0] = 1;
            x[1] = 2;
            x[2] = 3;
            write(x[1]);
            """;
        generateCode(input);
        assertEquals("2\n", runMain());
    }
    @Test
    public void testSimpleStringArray() throws Exception {
        String input =
            """
            string[] x = string[3];
            x[0] = "Hello";
            x[1] = "World";
            x[2] = "!";
            write(x[0] + x[1] + x[2]);
            """;
        generateCode(input);
        assertEquals("HelloWorld!\n", runMain());
    }
    @Test
    public void testSimpleStruct() throws Exception {
        String input =
            """
            struct Point {
                int x;
                int y;
            }
            Point p = Point(1,2);
            p.x = 42;
            p.y = 34;
            write(p.x + p.y);
            """;
        generateCode(input);
        assertEquals("76\n", runMain());
    }
    @Test
    public void testSimpleStructArray() throws Exception {
        String input =
            """
            struct Point {
                int x;
                int y;
            }
            Point[] p = Point[3];
            p[0] = Point(1,2);
            p[1] = Point(3,4);
            p[2] = Point(5,6);
            write(p[0].x + p[1].y + p[2].x);
            """;
        generateCode(input);
        assertEquals("10\n", runMain());
    }
    @Test
    public void testSimpleFunctionWithArray() throws Exception {
        String input =
                """
                def int add(int[] x) {
                    return x[0] + x[1] + x[2];
                }
                int[] x = int[3];
                x[0] = 1;
                x[1] = 2;
                x[2] = 3;
                writeln(add(x));
                """;
        generateCode(input);
        assertEquals("6\n", runMain());
    }
    @Test
    public void testComparaison() throws Exception {
        String input =
                """
                int x = 5;
                float y = 3.0;
                string z = "Hello";
                if (x > 5 && z == "Hello") {
                    writeln(1);
                } else {
                    writeln(0);
                }
                if (x >= 5 && z != "Hello") {
                    writeln(1);
                } else {
                    writeln(0);
                }
                if (x >= 5 && x > y) {
                    writeln(1);
                } else {
                    writeln(0);
                }
                if (x > 5 || x > y || y < 10 && y <= 4.0) {
                    writeln(1);
                } else {
                    writeln(0);
                }
                
                """;
        generateCode(input);
        assertEquals("0\n0\n1\n1\n", runMain());
    }


    ////////////////// Build In function tests //////////////////
    @Test
    public void testDiffWriteAndWriteln() throws Exception {
        String input =
                """
                int x = 5;
                write(x);
                writeln(x);
                """;
        generateCode(input);
        assertEquals("55\n", runMain());
    }
    @Test
    public void testAllWrite() throws Exception {
        String input =
                """
                int x = 5;
                float y = 3.0;
                string z = "Hello";
                writeInt(x);
                writeFloat(y);
                writeString(z);
                write(x);
                write(y);
                write(z);
                """;
        generateCode(input);
        assertEquals("5\n3.0\nHello\n53.0Hello\n", runMain());
    }
    @Test
    public void testLength() throws Exception {
        String input =
                """
                string x = "Hello";
                write(len(x));
                """;
        generateCode(input);
        assertEquals("5\n", runMain());
    }
    @Test
    public void testChr() throws Exception {
        String input =
                """
                write(chr(97));
                """;
        generateCode(input);
        assertEquals("a\n", runMain());
    }
    @Test
    public void testFloor() throws Exception {
        String input =
                """
                float x = 3.5;
                write(floor(x));
                """;
        generateCode(input);
        assertEquals("3\n", runMain());
    }

    @Test
    public void testImport() throws Exception {
        String input =
                """
                import "mini";
                write(5);
                helloMini();
                write(miniConst);
                """;
        generateCode(input);
        assertEquals("hello from mini\n5Hello mini function\n42\n", runMain());
    }
    @Test
    public void testMathLib() throws Exception {
        String input =
                """
                        import libs.math;
                        writeln(5);
                        writeFloat(abs(-5.0));
                        writeFloat(abs(5.0));
                        writeInt(max(5, 3));
                        writeInt(min(5, 3));
                        writeFloat(sin(3.14));
                        writeFloat(atan(1.0));
                        writeFloat(log10(100.0));
                        writeFloat(exp(1.0));
                        """;
        generateCode(input);
        assertEquals("5\n5.0\n5.0\n5\n3\n0.001592548\n0.7853982\n2.0\n2.7182817\n", runMain());
    }
    @Test
    public void testArrayLib() throws Exception {
        String input =
                """
                        import libs.array;
                        int[] x = int[3];
                        x[0] = 1;
                        x[1] = 2;
                        x[2] = 3;
                        writeInt(sumArray(x));
                        writeInt(productArray(x));
                        writeInt(maxArray(x));
                        writeInt(minArray(x));
                        writeInt(averageArray(x));
                        """;
        generateCode(input);
        assertEquals("6\n6\n3\n1\n2\n", runMain());
    }


    ////////////////// Advanced tests //////////////////
    @Test
    public void testComplexAssignment() throws Exception {
        String input =
            """
            int x = 5;
            int y = 3;
            int z = 0;
            z = x + y;
            write(z);
            """;
        generateCode(input);
        assertEquals("8\n", runMain());
    }
    @Test
    public void testComplexCalculation() throws Exception {
        String input =
            """
            int x = 5;
            int y = 3;
            int z = 0;
            z = x + y * 2;
            write(z);
            """;
        generateCode(input);
        assertEquals("11\n", runMain());
    }
    @Test
    public void testComplexIf() throws Exception {
        String input =
            """
            int x = 5;
            if (x == 5) {
                writeln(1);
            } else {
                writeln(0);
            }
            if (x == 4) {
                writeln(1);
            } else {
                writeln(0);
            }
            """;
        generateCode(input);
        assertEquals("1\n0\n", runMain());
    }
    @Test
    public void testComplexWhile() throws Exception {
        String input =
            """
            int x = 5;
            while (x > 0) {
                writeln(x);
                x = x - 1;
            }
            """;
        generateCode(input);
        assertEquals("5\n4\n3\n2\n1\n", runMain());
    }
    @Test
    public void testComplexStruct() throws Exception {
        String input =
            """
            struct Point {
                int x;
                int y;
            }
            struct location {
                Point p;
                int z;
            }
            location l = location(Point(1,2), 3);
            l.p.x = 42;
            l.p.y = 34;
            write(l.p.x + l.p.y + l.z);
            """;
        generateCode(input);
        assertEquals("79\n", runMain());
    }
    @Test
    public void testComplexStructArray() throws Exception {
        String input =
            """
            struct Point {
                int x;
                int y;
            }
            struct location {
                Point p;
                int z;
            }
            location[] l = location[3];
            l[0] = location(Point(1,2), 3);
            l[1] = location(Point(3,4), 5);
            l[2] = location(Point(5,6), 7);
            write(l[0].p.x + l[1].p.y + l[2].p.x + l[0].z + l[1].z + l[2].z);
            """;
        generateCode(input);
        assertEquals("25\n", runMain());
    }
    @Test
    public void testComplexIfElse() throws Exception {
        String input =
            """
            int x = 5;
            float y = 3.0;
            bool z = true;
            if (x == 5 && y == 2.0 || x > 4 && y < 4.0) { 
                writeln(1);
            } else {
                writeln(0);
            }
            if (x == 5 && y == 2.0 || x > 6 && y < 4.0) { 
                writeln(1);
            } else {
                writeln(0);
            }
            """;
        generateCode(input);
        assertEquals("1\n0\n", runMain());
    }
    @Test
    public void testComplexWhileIf() throws Exception {
        String input =
            """
            int x = 5;
            int y = 3;
            int z = 0;
            while (x > 0 && y > 0 || z == 0) {
                if (x == 5) {
                    writeln(1);
                } else {
                    writeln(0);
                }
                if (x == 1) {
                z = 1;
                }
                y = y - 1;
                x = x - 1;
            }
            """;
        generateCode(input);
        assertEquals("1\n0\n0\n0\n0\n", runMain());
    }
    @Test
    public void testComplexComparaisonCast() throws Exception {
        String input =
                """
                int x = 5;
                float y = 3.0;
                string z = "Hello";
                if (x > 5 && z == "Hello") {
                    writeln(1);
                } else {
                    writeln(0);
                }
                if (x >= 5 && x > y) {
                    writeln(1);
                } else {
                    writeln(0);
                }
                if (x > 5 || x > y || y < 10 && y <= 4.0) {
                    writeln(1);
                } else {
                    writeln(0);
                }
                
                """;
        generateCode(input);
        assertEquals("0\n1\n1\n", runMain());
    }
    @Test
    public void testComplexFunctionWithStruct() throws Exception {
        String input =
                """
                struct Point {
                    int x;
                    int y;
                }
                struct Location {
                    Point p;
                    int z;
                }
                def int add(Location l) {
                    return l.p.x + l.p.y + l.z;
                }
                Location l = Location(Point(1,2), 3);
                writeln(add(l));
                def Point getPoint(Location l) {
                    return l.p;
                }
                Point p = getPoint(l);
                writeln(p.x);
                writeln(p.y);
                """;
        generateCode(input);
        assertEquals("6\n1\n2\n", runMain());
    }
    @Test
    public void testComplexFunctionWithArrayOfStruct() throws Exception {
        String input =
                """
                struct Point {
                    int x;
                    int y;
                }
                struct Location {
                    Point p;
                    int z;
                }
                def int add(Location[] l) {
                    return l[0].p.x + l[1].p.y + l[2].p.x + l[0].z + l[1].z + l[2].z;
                }
                Location[] l = Location[3];
                l[0] = Location(Point(1,2), 3);
                l[1] = Location(Point(3,4), 5);
                l[2] = Location(Point(5,6), 7);
                writeln(add(l));
                """;
        generateCode(input);
        assertEquals("25\n", runMain());
    }
    @Test
    public void testComplexReturnArray() throws Exception {
        String input =
                """
                def int[] add1(int[] x) {
                    int i;
                    for (i = 0, i < len(x), i = i + 1) {
                        x[i] = x[i] + 1;
                    }
                    return x;
                }
                int[] x = int[3];
                x[0] = 1;
                x[1] = 2;
                x[2] = 3;
                int[] y = add1(x);
                writeln(y[0]);
                writeln(y[1]);
                writeln(y[2]);
                """;
        generateCode(input);
        assertEquals("2\n3\n4\n", runMain());
    }

    @Test
    public void testBangOperator() throws Exception {
        String input =
                """
                bool x = true;
                bool y = false;
                if (!(x)) {
                    writeln(1);
                } else {
                    writeln(0);
                }
                if (!(y)) {
                    writeln(1);
                } else {
                    writeln(0);
                }
                """;
        generateCode(input);
        assertEquals("0\n1\n", runMain());
    }

    @Test
    public void testComplexBangOperator() throws Exception {
        String input =
                """
                bool x = true;
                bool y = false;
                if (!(x && y)) {
                    writeln(1);
                } else {
                    writeln(0);
                }
                if (!(x || y)) {
                    writeln(1);
                } else {
                    writeln(0);
                }
                """;
        generateCode(input);
        assertEquals("1\n0\n", runMain());
    }

}