package me.jh316;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;

import me.jh316.storage.StorageProperties;
import me.jh316.storage.StorageService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Main {
    // A visitor class to visit methods and their variables
    // private static class MethodVisitor extends VoidVisitorAdapter<Void> {
    // @Override
    // public void visit(MethodDeclaration md, Void arg) {
    // super.visit(md, arg);

    // // Skip methods with @Test annotation
    // // if (md.getAnnotations().stream().anyMatch(a ->
    // // a.getNameAsString().equals("Test"))) {
    // // return;
    // // }

    // System.out.println("Method: " + md.getName());

    // // Visit all variable declarations in the method
    // md.findAll(VariableDeclarator.class).forEach(vd -> {
    // System.out.println("Variable: " + vd.getName() + ", Type: " + vd.getType());
    // });

    // // check if a variable extends a class
    // md.findAll(VariableDeclarator.class).forEach(vd -> {
    // if (vd.getType().isClassOrInterfaceType()) {
    // System.out.println("Variable: " + vd.getName() + ", Extends: "
    // + vd.getType().asClassOrInterfaceType().getName());
    // }
    // });
    // }
    // }
    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        private static final Set<String> FORBIDDEN_METHODS = new HashSet<>(Arrays.asList(
                "System.exit", "toCharArray", "join", "matches", "Arrays.fill", "Arrays.copyOf",
                "Arrays.copyOfRange", "Arrays.sort", "toArray", "clone", "Collections.copy", "Collections.sort",
                "System.console"));

        private static final Set<String> STREAM_METHODS = new HashSet<>(Arrays.asList(
                "stream", "map", "filter", "forEach", "collect", "flatMap", "reduce", "distinct",
                "sorted", "peek", "limit", "skip", "anyMatch", "allMatch", "noneMatch", "findFirst", "findAny"));

        public static String forbiddenMessage = "";
        public static boolean goodconstructor = true;
        public static boolean goodmethod = true;

        @Override
        public void visit(ConstructorDeclaration cd, Void arg) {
            goodconstructor = false;
            super.visit(cd, arg);
            if (cd.isAnnotationPresent("Test")) {
                System.out.println("Method: " + cd.getName() + " is a test method, skipping...");
                return;
            }

            if (!containsForbiddenFeature(cd).isEmpty()) {
                System.out.println();
                System.out.println(
                        ANSI_RED +
                                "FORBIDDEN FEATURE IN FILE: "
                                + cd.findCompilationUnit().get().getStorage().get().getPath().getFileName()
                                + " METHOD: " + cd.getName());
                System.out.println(containsForbiddenFeature(cd) + ANSI_RESET);
                System.out.println();

                forbiddenMessage += "FORBIDDEN FEATURE IN FILE: "
                        + cd.findCompilationUnit().get().getStorage().get().getPath().getFileName()
                        + " METHOD: " + cd.getName() + "\n";
                forbiddenMessage += containsForbiddenFeature(cd) + "\n";
                return;
            } else {
                goodconstructor = true;
            }
        }

        private List<String> containsForbiddenFeature(ConstructorDeclaration md) {
            List<String> ret = new ArrayList<>();

            if (md.findAll(BreakStmt.class).size() > 0 || md.findAll(ContinueStmt.class).size() > 0) {
                // System.out.println("Method: " + md.getName() + " contains break or continue
                // statements!");
                ret.add("Method: " + md.getName() + " contains break or continue statements!");
            }

            // if (md.findAll(ReturnStmt.class).size() > 0) {
            // System.out.println("Method: " + md.getName() + " contains return statements
            // in a void method!");
            // ret.add("Method: " + md.getName() + " contains return statements in a void
            // method!");
            // }

            if (md.findAll(SwitchStmt.class).size() > 0 || md.findAll(TryStmt.class).size() > 0) {
                // System.out.println("Method: " + md.getName() + " contains switch or try
                // statements!");
                ret.add("Method: " + md.getName() + " contains switch or try statements!");
            }

            if (md.getModifiers().stream().anyMatch(modifier -> modifier.getKeyword().asString().equals("protected"))) {
                // System.out.println("Method: " + md.getName() + " is protected!");
                ret.add("Method: " + md.getName() + " is protected!");
            }

            if (md.findAll(ObjectCreationExpr.class).stream()
                    .anyMatch(oce -> oce.getType().asString()
                            .matches("StringBuilder|StringBuffer|StringJoiner|StringTokenizer"))) {
                // System.out.println("Method: " + md.getName() + " contains forbidden object
                // creation!");
                ret.add("Method: " + md.getName() + " contains forbidden object creation of one of: " + "StringBuilder"
                        + ", " + "StringBuffer" + ", " + "StringJoiner" + ", " + "StringTokenizer");
            }

            // LinkedHashMap type checker
            if (md.findAll(ObjectCreationExpr.class).stream()
                    .anyMatch(oce -> oce.getType().asString().startsWith("LinkedHashMap"))) {
                // System.out.println("Method: " + md.getName() + " contains LinkedHashMap
                // object creation!");
                ret.add("Method: " + md.getName() + " contains LinkedHashMap object creation!");
            }

            if (md.findAll(MethodCallExpr.class).stream()
                    .anyMatch(mce -> FORBIDDEN_METHODS.contains(mce.getNameAsString())
                            || FORBIDDEN_METHODS.contains(
                                    mce.getScope().map(Node::toString).orElse("") + "." + mce.getNameAsString()))) {
                // System.out.println("Method: " + md.getName() + " contains forbidden method
                // calls!");
                ret.add("Method: " + md.getName() + " contains forbidden method calls!");
            }

            // if (md.getAnnotations().stream().anyMatch(a ->
            // a.getNameAsString().equals("Override"))) {
            // System.out.println("Method: " + md.getName() + " is an overridden method!");
            // return tfue;
            // }

            // Check for var keyword
            if (md.findAll(VariableDeclarator.class).stream()
                    .anyMatch(vd -> vd.getTypeAsString().equals("var"))) {
                // System.out.println("Method: " + md.getName() + " contains var keyword!");
                ret.add("Method: " + md.getName() + " contains var keyword!");
            }
            // Check for lambda expressions
            if (md.findAll(LambdaExpr.class).size() > 0) {
                ret.add("Method: " + md.getName() + " contains lambda expressions!");
            }

            // Check for method references
            if (md.findAll(MethodReferenceExpr.class).size() > 0) {
                // System.out.println("Method: " + md.getName() + " contains method
                // references!");
                ret.add("Method: " + md.getName() + " contains method references!");
            }

            // Check for stream API usage
            if (md.findAll(MethodCallExpr.class).stream()
                    .anyMatch(mce -> STREAM_METHODS.contains(mce.getNameAsString()))) {
                // System.out.println("Method: " + md.getName() + " contains stream API
                // usage!");
                ret.add("Method: " + md.getName() + " contains stream API usage!");
            }

            // Check for Java 8 and Java 11 specific features
            if (md.findAll(MethodCallExpr.class).stream()
                    .anyMatch(mce -> mce.getNameAsString()
                            .matches("repeat|strip|lines|isBlank|stripLeading|stripTrailing"))) {
                // System.out.println("Method: " + md.getName() + " contains Java 11 specific
                // features!");
                ret.add("Method: " + md.getName() + " contains Java 11 specific features!");
            }

            return ret;
        }

        private List<String> containsForbiddenFeature(MethodDeclaration md) {
            List<String> ret = new ArrayList<>();

            if (md.findAll(BreakStmt.class).size() > 0 || md.findAll(ContinueStmt.class).size() > 0) {
                // System.out.println("Method: " + md.getName() + " contains break or continue
                // statements!");
                ret.add("Method: " + md.getName() + " contains break or continue statements!");
            }

            if (md.getType().isVoidType() && md.findAll(ReturnStmt.class).size() > 0) {
                // System.out.println("Method: " + md.getName() + " contains return statements
                // in a void method!");
                ret.add("Method: " + md.getName() + " contains return statements in a void method!");
            }

            if (md.findAll(SwitchStmt.class).size() > 0 || md.findAll(TryStmt.class).size() > 0) {
                // System.out.println("Method: " + md.getName() + " contains switch or try
                // statements!");
                ret.add("Method: " + md.getName() + " contains switch or try statements!");
            }

            if (md.getModifiers().stream().anyMatch(modifier -> modifier.getKeyword().asString().equals("protected"))) {
                // System.out.println("Method: " + md.getName() + " is protected!");
                ret.add("Method: " + md.getName() + " is protected!");
            }

            if (md.findAll(ObjectCreationExpr.class).stream()
                    .anyMatch(oce -> oce.getType().asString()
                            .matches("StringBuilder|StringBuffer|StringJoiner|StringTokenizer"))) {
                // System.out.println("Method: " + md.getName() + " contains forbidden object
                // creation!");
                ret.add("Method: " + md.getName() + " contains forbidden object creation of one of: " + "StringBuilder"
                        + ", " + "StringBuffer" + ", " + "StringJoiner" + ", " + "StringTokenizer");
            }

            // LinkedHashMap type checker
            if (md.findAll(ObjectCreationExpr.class).stream()
                    .anyMatch(oce -> oce.getType().asString().startsWith("LinkedHashMap"))) {
                // System.out.println("Method: " + md.getName() + " contains LinkedHashMap
                // object creation!");
                ret.add("Method: " + md.getName() + " contains LinkedHashMap object creation!");
            }

            if (md.findAll(MethodCallExpr.class).stream()
                    .anyMatch(mce -> FORBIDDEN_METHODS.contains(mce.getNameAsString())
                            || FORBIDDEN_METHODS.contains(
                                    mce.getScope().map(Node::toString).orElse("") + "." + mce.getNameAsString()))) {
                // System.out.println("Method: " + md.getName() + " contains forbidden method
                // calls!");
                ret.add("Method: " + md.getName() + " contains forbidden method calls!");
            }

            // if (md.getAnnotations().stream().anyMatch(a ->
            // a.getNameAsString().equals("Override"))) {
            // System.out.println("Method: " + md.getName() + " is an overridden method!");
            // return tfue;
            // }

            // Check for var keyword
            if (md.findAll(VariableDeclarator.class).stream()
                    .anyMatch(vd -> vd.getTypeAsString().equals("var"))) {
                // System.out.println("Method: " + md.getName() + " contains var keyword!");
                ret.add("Method: " + md.getName() + " contains var keyword!");
            }
            // Check for lambda expressions
            if (md.findAll(LambdaExpr.class).size() > 0) {
                ret.add("Method: " + md.getName() + " contains lambda expressions!");
            }

            // Check for method references
            if (md.findAll(MethodReferenceExpr.class).size() > 0) {
                // System.out.println("Method: " + md.getName() + " contains method
                // references!");
                ret.add("Method: " + md.getName() + " contains method references!");
            }

            // Check for stream API usage
            if (md.findAll(MethodCallExpr.class).stream()
                    .anyMatch(mce -> STREAM_METHODS.contains(mce.getNameAsString()))) {
                // System.out.println("Method: " + md.getName() + " contains stream API
                // usage!");
                ret.add("Method: " + md.getName() + " contains stream API usage!");
            }

            // Check for Java 8 and Java 11 specific features
            if (md.findAll(MethodCallExpr.class).stream()
                    .anyMatch(mce -> mce.getNameAsString()
                            .matches("repeat|strip|lines|isBlank|stripLeading|stripTrailing"))) {
                // System.out.println("Method: " + md.getName() + " contains Java 11 specific
                // features!");
                ret.add("Method: " + md.getName() + " contains Java 11 specific features!");
            }

            return ret;
        }

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            goodmethod = false;
            super.visit(md, arg);
            if (md.isAnnotationPresent("Test")) {
                System.out.println("Method: " + md.getName() + " is a test method, skipping...");
                return;
            }

            if (!containsForbiddenFeature(md).isEmpty()) {
                System.out.println();
                System.out.println(
                        ANSI_RED +
                                "FORBIDDEN FEATURE IN FILE: "
                                + md.findCompilationUnit().get().getStorage().get().getPath().getFileName()
                                + " METHOD: " + md.getName());
                System.out.println(containsForbiddenFeature(md) + ANSI_RESET);
                System.out.println();
                forbiddenMessage += "FORBIDDEN FEATURE IN FILE: "
                        + md.findCompilationUnit().get().getStorage().get().getPath().getFileName()
                        + " METHOD: " + md.getName() + "\n";
                forbiddenMessage += containsForbiddenFeature(md) + "\n";
                return;
            } else {
                goodmethod = true;
            }
        }

    }

    // System.out.println("Method: " + md.getName());

    // Visit all variable declarations in the method
    // md.findAll(VariableDeclarator.class).forEach(vd -> {
    // System.out.println("Variable: " + vd.getName() + ", Type: " + vd.getType());
    // });

    private static boolean containsForbiddenPackageDeclaration(CompilationUnit cu) {
        return cu.getPackageDeclaration().isPresent();
    }

    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_RED = "\u001B[31m\t";

    public static final String ANSI_GREEN = "\u001B[32m\t";

    public static String hasForbidden(String path) throws IOException {
        MethodVisitor.forbiddenMessage = "";
        MethodVisitor.goodconstructor = true;
        MethodVisitor.goodmethod = true;
        // Specify the source code root directory
        SourceRoot sourceRoot = new SourceRoot(Paths.get(path));

        // Parse all Java files in the directory
        sourceRoot.tryToParse();

        // For each parsed file, visit the methods and variables
        boolean hasForbiddenFeature = false;
        for (CompilationUnit cu : sourceRoot.getCompilationUnits()) {
            if (containsForbiddenPackageDeclaration(cu)) {
                System.out.println(
                        ANSI_RED + "FORBIDDEN FEATURE IN FILE: " + cu.getStorage().get().getPath().getFileName()
                                + " PACKAGE DECLARATION" + ANSI_RESET);
                hasForbiddenFeature = true;
                MethodVisitor.forbiddenMessage += "FORBIDDEN FEATURE IN FILE: "
                        + cu.getStorage().get().getPath().getFileName() + " PACKAGE DECLARATION" + "\n";
                MethodVisitor.forbiddenMessage += "Package declaration is forbidden\n";
                continue;
            }
            cu.accept(new MethodVisitor(), null);
        }

        if (!hasForbiddenFeature) {
            System.out.println(ANSI_GREEN + "No forbidden features found in methods!" + ANSI_RESET);
        }
        if (MethodVisitor.goodconstructor && MethodVisitor.goodmethod && MethodVisitor.forbiddenMessage.isEmpty()) {
            MethodVisitor.forbiddenMessage = "No forbidden features found in methods!";
        }
        return MethodVisitor.forbiddenMessage;
    }

    public static void main(String[] args) throws Exception {
        // Specify the source code root directory
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}