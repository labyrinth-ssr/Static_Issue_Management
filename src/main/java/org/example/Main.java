package org.example;

import org.example.Query.Cmd;
import org.example.Update.Save;

public class Main {
    public static void main(String[] args) throws Exception {
//        Cmd.run();
        Save.save(Constant.RepoPath);
    }
}