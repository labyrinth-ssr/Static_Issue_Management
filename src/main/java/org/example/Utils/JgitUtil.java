package org.example.Utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.example.Entity.Commit;
import org.example.Entity.Iss_file;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * 实现Jgit操作
 */
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

    public static Ref gitReset(Git git, String commit_hash){
        try {
            RevWalk walk = new RevWalk(git.getRepository());    //获取walk对象
            Ref ref = git.reset().setMode(ResetCommand.ResetType.HARD).setRef(commit_hash).call();  //设置参数
            return ref;
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public static Commit RevCommit2Commit(RevCommit c){
        Commit commit = new Commit();
        commit.setCommit_hash(c.getName());
        commit.setCommitter(c.getAuthorIdent().getName());
        commit.setCommitter_email(c.getAuthorIdent().getEmailAddress());
        final Instant commitInstant = Instant.ofEpochSecond(c.getCommitTime());
        final ZoneId zoneId = c.getAuthorIdent().getTimeZone().toZoneId();
        final ZonedDateTime authorDateTime = ZonedDateTime.ofInstant(commitInstant, zoneId);
        final String gitDateTimeFormatString = "yyyy MM dd HH:mm:ss Z";
        final String formattedDate = authorDateTime.format(DateTimeFormatter.ofPattern(gitDateTimeFormatString));
        commit.setCommit_time(formattedDate);
        commit.setCommit_msg(c.getShortMessage());
        if(c.getParentCount() > 0) commit.setParent_commit_hash(c.getParent(0).getName());
        return  commit;
    }

    public static Commit gitCurLog(Git git){
        try {
            Repository repo = git.getRepository();
            RevWalk revWalk = new RevWalk(repo);
            ObjectId objectId = repo.resolve(Constants.HEAD);
            RevCommit revCommit = revWalk.parseCommit(objectId);
            return RevCommit2Commit(revCommit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Commit> gitLog(Git git){
        try {
            Iterable<RevCommit> logs = git.log().all().call();
            List<Commit> commitList = new ArrayList<>();
            logs.forEach(c->{
                Commit commit = RevCommit2Commit(c);
                commitList.add(commit);
            });
            return commitList;
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Iss_file> gitFileList(Git git,String repo_name){

        List<Iss_file> ret=new ArrayList<>();
        try {
            Repository repo = git.getRepository();
            RevWalk revWalk = new RevWalk(repo);
            ObjectId objectId = repo.resolve(Constants.HEAD);
            RevCommit revCommit = revWalk.parseCommit(objectId);

            revCommit.getCommitTime();

            ObjectId treeId = revCommit.getTree().getId();

            try (TreeWalk treeWalk = new TreeWalk(repo)) {
                treeWalk.reset(treeId);
                treeWalk.setRecursive(true);
                while (treeWalk.next()) {
                    Iss_file iss_file = new Iss_file();
                    iss_file.setFile_path(treeWalk.getPathString());
                    iss_file.path_to_name();
                    iss_file.setRepo_path(repo_name);
                    iss_file.setCreated_time(String.valueOf(revCommit.getCommitTime()));
                    ret.add(iss_file);
                }
                return ret;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
