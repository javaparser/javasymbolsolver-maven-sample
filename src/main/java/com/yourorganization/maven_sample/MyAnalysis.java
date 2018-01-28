package com.yourorganization.maven_sample;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseStart;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StringProvider;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.IOException;

/**
 * Some code that uses JavaSymbolSolver.
 */
public class MyAnalysis {

    private static JavaParser settingUpSymbolResolution() {
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

        // Configure JavaParser to use type resolution
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));
        return new JavaParser(parserConfiguration);
    }

    public static void main(String[] args) throws IOException {
        JavaParser javaParser = settingUpSymbolResolution();

        // Parse some code
        CompilationUnit cu = javaParser.parse(ParseStart.COMPILATION_UNIT,
                new StringProvider("class X { int x() { return 1 + 1.0; } }")).getResult().get();

        // Find the expression 1+1.0
        ReturnStmt returnStatement = (ReturnStmt) cu
                .getClassByName("X").get()
                .getMethods().get(0)
                .getBody().get().getStatements().get(0);
        Expression onePlusOne = returnStatement.getExpression().get();

        // Find out what type the result of 1+1.0 has.
        ResolvedType typeOfOnePlusOne = onePlusOne.calculateResolvedType();

        // Show that it's "double"
        System.out.println(typeOfOnePlusOne);
    }
}
