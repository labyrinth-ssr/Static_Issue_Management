package org.example.Utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class JgitUtil {
    public static Git openRpo(String dir){
        Git git = null;
        try {
            Repository repository = new FileRepositoryBuilder()
                    .setGitDir(Paths.get(dir, ".git").toFile())
                    .build();
            git = new Git(repository);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return git;
    }

    public static List<Ref> gitBranchList(Git git){
        try {
            return git.branchList().call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public static void gitReset(Git git){

        try {
            RevWalk walk = new RevWalk(git.getRepository());    //获取walk对象
            ObjectId objectId = git.getRepository().resolve("9167dadd48bca196208a4687b5d4706e355c42b6");   //ObjectId对象
            RevCommit revCommit = walk.parseCommit(objectId);   //获取Revcommit对象
            String perVision = revCommit.getParent(0).getName();   //获取commit的身份名
            git.reset().setMode(ResetCommand.ResetType.HARD).setRef(perVision).call();  //设置参数
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    //public static void gitCheckout(Git git, )
}
