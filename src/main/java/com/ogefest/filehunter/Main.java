package com.ogefest.filehunter;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main implements QuarkusApplication {

//    public static void main(String ... args) {
//        System.out.println("Running main method");
//        Quarkus.run(args);
//    }

    @Override
    public int run(String... args) throws Exception {
        Configuration.setArgs(args);
        Quarkus.waitForExit();

        return 0;
    }
}
