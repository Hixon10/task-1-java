package ru.spbau.pavlyutchenko.task1;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@SupportedAnnotationTypes("ru.spbau.pavlyutchenko.task1.TestClass")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        HashMap<String, ArrayList<String>> testUnits = new HashMap<>();

        for (Element element : roundEnv.getRootElements()) {
            if (element.getKind() != ElementKind.CLASS) {
                error(element, "Not a class");
                return true;
            }

            TypeElement typeElement = (TypeElement) element;

            if (typeElement.getModifiers().contains(Modifier.ABSTRACT) || typeElement.getModifiers().contains(Modifier.PRIVATE)) {
                error(typeElement, "Abstract or private class " + typeElement.toString());
                return true;
            }

            TestClass testClass = typeElement.getAnnotation(TestClass.class);
            if (testClass != null) {
                String fullClassName = typeElement.getQualifiedName().toString();
                String simpleClassName = typeElement.getSimpleName().toString();
                String testUnit = testClass.testUnit();
                StringBuilder tests = new StringBuilder();

                ArrayList<String> classes = testUnits.get(testUnit);
                if (classes != null) {
                    classes.add(simpleClassName);
                } else {
                    classes = new ArrayList<>();
                    classes.add(simpleClassName);
                    testUnits.put(testUnit, classes);
                }

                for (Element encElement: typeElement.getEnclosedElements()) {
                    if (encElement.getKind() != ElementKind.METHOD) {
                        continue;
                    }
                    ExecutableElement method = (ExecutableElement) encElement;
                    Test test = method.getAnnotation(Test.class);
                    if (test != null) {
                        final Set<Modifier> modifiers = method.getModifiers();
                        if (!modifiers.contains(Modifier.STATIC) || !modifiers.contains(Modifier.PUBLIC) || modifiers.contains(Modifier.ABSTRACT)) {
                            error(method, "Not static or abstract method " + Test.class.getSimpleName());
                            return true;
                        }

                        tests.append(renderTest(method, fullClassName, test.testLevel().toString()));
                    }
                }

                saveTestFile(tests.toString(), simpleClassName);
            }
        }

        if (!annotations.isEmpty()) {
            for (Map.Entry<String, ArrayList<String>> tUnit : testUnits.entrySet()) {
                saveTestUnit(tUnit.getKey(), tUnit.getValue());
            }
        }
        return true;
    }

    private void saveTestUnit(String testUnitName, ArrayList<String> classes) {
        StringBuilder classesString = new StringBuilder();

        for (String cls : classes) {
            classesString.append("        " + cls + "Test.run(testLevel);" + System.lineSeparator());
        }

        String resultString = "" +
                "package ru.spbau.pavlyutchenko.task1;" + System.lineSeparator() +

                "import ru.spbau.pavlyutchenko.task1.TestLevel;" + System.lineSeparator() +

                "public class " + testUnitName + " {" + System.lineSeparator() +
                "    public static void main(String[] args) {" + System.lineSeparator() +
                "        TestLevel testLevel = TestLevel.LOW;" + System.lineSeparator() +

                "        if (args.length > 0) {" + System.lineSeparator() +
                "            testLevel = TestLevel.valueOf(TestLevel.class, args[0]);" + System.lineSeparator() +
                "        }" + System.lineSeparator() +
                classesString.toString() +
                "    }" + System.lineSeparator() +
                "}";

        FileObject file;
        try {
            file = filer.createSourceFile(testUnitName);
            try (Writer writer = file.openWriter()) {
                writer.write(resultString);
            }
        } catch (IOException e) {
            error("Error during writing class " + testUnitName + "Test" + ": " + e.getMessage());
        }
    }

    private String saveTestFile(String tests, String className) {
        String fileString = "" +
                "package ru.spbau.pavlyutchenko.task1;" + System.lineSeparator() +
                "import java.util.HashMap;" + System.lineSeparator() +
                "import java.util.Map;" + System.lineSeparator() +
                "import java.util.concurrent.Callable;" + System.lineSeparator() +
                "import ru.spbau.pavlyutchenko.task1.TestLevel;" + System.lineSeparator() +

                "public class " + className + "Test {" + System.lineSeparator() +
                "    public static HashMap<Callable, TestLevel> tests = new HashMap<Callable, TestLevel>();" + System.lineSeparator() +
                "    static {" + System.lineSeparator() +
                     tests +
                "    }" + System.lineSeparator() +

                "    public static void run(TestLevel level) {" + System.lineSeparator() +
                "        System.out.println(\"" + className + "\");" + System.lineSeparator() +

                "        for (Map.Entry<Callable, TestLevel> test : tests.entrySet()) {" + System.lineSeparator() +
                "            if (level.level <= test.getValue().level) {" + System.lineSeparator() +
                "                try {" + System.lineSeparator() +
                "                    test.getKey().call();" + System.lineSeparator() +
                "                } catch (Exception e) {" + System.lineSeparator() +
                "                    System.out.println(\"Error: \" + e.getMessage());" + System.lineSeparator() +
                "                }" + System.lineSeparator() +
                "            }" + System.lineSeparator() +
                "        }" + System.lineSeparator() +
                "    }" + System.lineSeparator() +
                "}" + System.lineSeparator();

        FileObject file;
        try {
            file = filer.createSourceFile(className + "Test");
            try (Writer writer = file.openWriter()) {
                writer.write(fileString);
            }
        } catch (IOException e) {
            error("Error during writing class " + className + "Test" + ": " + e.getMessage());
        }

        return fileString;
    }

    private String renderTest(ExecutableElement method, String className, String testLevel) {
        String result = "" + System.lineSeparator() +
          "tests.put(new Callable() {" + System.lineSeparator() +
            "public Object call() throws Exception {" + System.lineSeparator() +
                "System.out.print(\"" + method.getSimpleName() + "Test" + "\");" + System.lineSeparator() +
                "try {" + System.lineSeparator() +
                className + "." + method.toString() + ";" + System.lineSeparator() +
                "System.out.println(\" passed\");" + System.lineSeparator() +
                "} catch (Exception e) {" + System.lineSeparator() +
                "System.out.println(\" Error: \" + e.getMessage());" + System.lineSeparator() +
                "}" + System.lineSeparator() +
                "return null;" + System.lineSeparator() +
                "}" + System.lineSeparator() +
                "}, TestLevel." + testLevel + ");" + System.lineSeparator()
                + "" + System.lineSeparator();

        return result;
    }

    private void error(Element e, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, e);
    }

    private void error(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }
}
