package ru.spbau.pavlyutchenko.task1;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("ru.spbau.pavlyutchenko.task1.TestSpeed")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TestSpeedProcessor extends AbstractProcessor {

    private Messager messager;
    private TreeMaker treeMaker;
    private JavacElements utils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();

        JavacProcessingEnvironment javacProcessingEnv = (JavacProcessingEnvironment) processingEnv;
        Context context = javacProcessingEnv.getContext();
        treeMaker = TreeMaker.instance(context);
        utils = javacProcessingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(TestSpeed.class)) {
            if (element.getKind() != ElementKind.METHOD) {
                error(element, "Not a method");
                return true;
            }

            ExecutableElement methodElement = (ExecutableElement) element;
            if (methodElement.getModifiers().contains(Modifier.ABSTRACT)) {
                error(element, "Abstract method");
                return true;
            }

            JCMethodDecl methodNode = (JCMethodDecl) utils.getTree(methodElement);
            List<JCStatement> currentMethodBody = List.from(methodNode.body.stats);
            String methodName = utils.getPackageOf(methodElement).getQualifiedName() + "." + methodElement.getSimpleName().toString();

            methodNode.body.stats = makeMethodBodyWithElapsedTime(currentMethodBody, methodName);
        }
        return true;
    }

    private List<JCStatement> makeMethodBodyWithElapsedTime(List<JCStatement> currentMethodBody, String methodName) {
        JCExpression println = treeMaker.Ident(utils.getName("System"));
        println = treeMaker.Select(println, utils.getName("out"));
        println = treeMaker.Select(println, utils.getName("println"));

        JCExpression currentTime = getCurrentTime();
        JCVariableDecl startTime = treeMaker.VarDef(treeMaker.Modifiers(Flags.FINAL), utils.getName("startTime"), treeMaker.TypeIdent(TypeTag.LONG), currentTime);
        List<JCStatement> resultBody = List.of(startTime);

        currentTime = getCurrentTime();
        JCBinary elapsed = treeMaker.Binary(Tag.MINUS, currentTime, treeMaker.Ident(startTime.name));

        List<JCExpression> bodyExpression = List.of(treeMaker.Binary(Tag.PLUS, treeMaker.Literal(methodName + " "), elapsed));
        JCMethodInvocation printInvocation = treeMaker.Apply(List.nil(), println, bodyExpression);

        JCBlock resultBlock = treeMaker.Block(0, List.of(treeMaker.Exec(printInvocation)));
        return resultBody.append(treeMaker.Try(treeMaker.Block(0, currentMethodBody), List.nil(), resultBlock));
    }

    private JCExpression getCurrentTime() {
        JCExpression expression = treeMaker.Ident(utils.getName("System"));
        expression = treeMaker.Select(expression, utils.getName("currentTimeMillis"));
        return treeMaker.Apply(List.nil(), expression, List.nil());
    }

    private void error(Element e, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, e);
    }

}