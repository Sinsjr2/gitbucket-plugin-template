package io.github.gitbucket.helloworld.controller

import gitbucket.core.controller.ControllerBase
import gitbucket.core.service.IssuesService
import gitbucket.core.service.RepositoryService
import gitbucket.core.service.RepositoryService.RepositoryInfo
import gitbucket.core.util.ReferrerAuthenticator
import gitbucket.core.service.AccountService
import gitbucket.core.service.LabelsService
import gitbucket.core.service.MilestonesService
import gitbucket.core.service.ActivityService
import gitbucket.core.service.IssueCreationService
import gitbucket.core.service.WebHookIssueCommentService
import gitbucket.core.util.ReadableUsersAuthenticator
import gitbucket.core.service.WebHookPullRequestService
import gitbucket.core.util.WritableUsersAuthenticator
import gitbucket.core.service.PrioritiesService
import gitbucket.core.service.HandleCommentService
import gitbucket.core.service.PullRequestService
import gitbucket.core.service.CommitsService
import gitbucket.core.service.WebHookService
import gitbucket.core.service.MergeService
import gitbucket.core.service.WebHookPullRequestReviewCommentService
import gitbucket.core.service.RequestCache
import org.scalatra._
import gitbucket.access.html

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import gitbucket.core.plugin.PluginRegistry
import gitbucket.core.repo.html
import gitbucket.core.helper
import gitbucket.core.service._
import gitbucket.core.util._
import gitbucket.core.util.StringUtil._
import gitbucket.core.util.SyntaxSugars._
import gitbucket.core.util.Implicits._
import gitbucket.core.util.Directory._
import gitbucket.core.model.{Account, CommitState, CommitStatus}
import gitbucket.core.util.JGitUtil.CommitInfo
import gitbucket.core.view
import gitbucket.core.view.helpers
import org.apache.commons.compress.archivers.{ArchiveEntry, ArchiveOutputStream}
import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveOutputStream}
import org.apache.commons.compress.archivers.zip.{ZipArchiveEntry, ZipArchiveOutputStream}
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream
import org.apache.commons.compress.utils.IOUtils
import org.apache.commons.io.FileUtils
import org.scalatra.forms._
import org.eclipse.jgit.api.{ArchiveCommand, Git}
import org.eclipse.jgit.archive.{TgzFormat, ZipFormat}
import org.eclipse.jgit.errors.MissingObjectException
import org.eclipse.jgit.lib._
import org.eclipse.jgit.treewalk.{TreeWalk, WorkingTreeOptions}
import org.eclipse.jgit.treewalk.TreeWalk.OperationType
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.util.io.EolStreamTypeUtil
import org.json4s.jackson.Serialization
import org.scalatra._
import org.scalatra.i18n.Messages
import gitbucket.core.controller.AccountManagementControllerBase
import gitbucket.core.controller.IssuesControllerBase
import gitbucket.core.controller.Context
import gitbucket.core.model.Profile.profile.blockingApi._
import gitbucket.core.controller.RepositoryViewerController
import gitbucket.plugin.service.AccessService
import gitbucket.core.model.Collaborator


trait HogeReferrerAuthenticator {
  self: ControllerBase with RepositoryService =>
  protected def referrersOnly(action: (RepositoryInfo) => Any) = {
    defining(request.paths) { paths =>
         getRepository(paths(0), paths(1)).map { repository =>
           action(repository)
          }
        } getOrElse NotFound
    }
  // protected override def referrersOnly[T](action: (T, RepositoryInfo) => Any) = (form: T) => { authenticate(action(form, _)) }

}

trait IssueAcess extends ControllerBase {
    self : IssuesService
      with RepositoryService
    //with AccountService
    //with LabelsService
    //with MilestonesService
    //with ActivityService
    //with HandleCommentService
    with IssueCreationService
    with ReadableUsersAuthenticator
    with ReferrerAuthenticator
    //with HogeReferrerAuthenticator
    with WritableUsersAuthenticator
    //with PullRequestService
    //with WebHookIssueCommentService
    //with CommitsService
    //with PrioritiesService
     =>

  override def referrersOnly(action: (RepositoryInfo) => Any) = {
    defining(request.paths) { paths =>
         getRepository(paths(0), paths(1)).map { repository =>
           action(repository)
          }
        } getOrElse NotFound
    }
  override def referrersOnly[T](action: (T, RepositoryInfo) => Any) = (form: T) => { 
    defining(request.paths) { paths =>
         getRepository(paths(0), paths(1)).map { repository =>
           action(form, repository)
          }
        } getOrElse NotFound
   }
  override def writableUsersOnly(action: (RepositoryInfo) => Any) = { referrersOnly(action) }
  override def writableUsersOnly[T](action: (T, RepositoryInfo) => Any) = (form: T) => { referrersOnly(action)(form) }

  override def readableUsersOnly(action: (RepositoryInfo) => Any) = { referrersOnly(action) }
  override def readableUsersOnly[T](action: (T, RepositoryInfo) => Any) = (form: T) => { referrersOnly(action)(form) } 
  override def isIssueEditable(repository: RepositoryInfo)(implicit context: Context, s: Session): Boolean = {
        // ログインしている必要がある。
        // 条件分岐が必要
    true
  }

  override def isIssueManageable(repository: RepositoryInfo)(implicit context: Context, s: Session): Boolean = {
    true
  }

}

class Hoge extends IssuesControllerBase
    with IssuesService
    with RepositoryService
    with AccountService
    with LabelsService
    with MilestonesService
    with ActivityService
    with HandleCommentService
    with IssueCreationService
    with ReadableUsersAuthenticator
    with ReferrerAuthenticator
    //with HogeReferrerAuthenticator
    with WritableUsersAuthenticator
    with PullRequestService
    with WebHookIssueCommentService
    with CommitsService
    with PrioritiesService
 {
  override def referrersOnly(action: (RepositoryInfo) => Any) = {
    defining(request.paths) { paths =>
         getRepository(paths(0), paths(1)).map { repository =>
           action(repository)
          }
        } getOrElse NotFound
    }
  override def referrersOnly[T](action: (T, RepositoryInfo) => Any) = (form: T) => { 
    defining(request.paths) { paths =>
         getRepository(paths(0), paths(1)).map { repository =>
           action(form, repository)
          }
        } getOrElse NotFound
   }
  override def writableUsersOnly(action: (RepositoryInfo) => Any) = { referrersOnly(action) }
  override def writableUsersOnly[T](action: (T, RepositoryInfo) => Any) = (form: T) => { referrersOnly(action)(form) }

  override def readableUsersOnly(action: (RepositoryInfo) => Any) = { referrersOnly(action) }
  override def readableUsersOnly[T](action: (T, RepositoryInfo) => Any) = (form: T) => { referrersOnly(action)(form) } 
  override def isIssueEditable(repository: RepositoryInfo)(implicit context: Context, s: Session): Boolean = {
        // ログインしている必要がある。
        // 条件分岐が必要
        return context.loginAccount.isDefined
  }

  override def isIssueManageable(repository: RepositoryInfo)(implicit context: Context, s: Session): Boolean = {
    return false // 自身で作成したissueでなくても、編集できるかどうか (管理者用) issueだけを編集可能にするグループであるとfalseを返す必要がある
  }
}

trait MyPreview extends RepositoryViewerController {
  //self : with ReferrerAuthenticator
  //=>
    override def referrersOnly(action: (RepositoryInfo) => Any) = {
    defining(request.paths) { paths =>
         getRepository(paths(0), paths(1)).map { repository =>
           action(repository)
          }
        } getOrElse NotFound
    }
  override def referrersOnly[T](action: (T, RepositoryInfo) => Any) = (form: T) => { 
    defining(request.paths) { paths =>
         getRepository(paths(0), paths(1)).map { repository =>
           action(form, repository)
          }
        } getOrElse NotFound
   }
}

class HelloWorldController extends HelloWorldControllerBase
  with AccountManagementControllerBase
  with gitbucket.core.service.LabelsService with gitbucket.core.service.PrioritiesService with gitbucket.core.service.MilestonesService
  with IssuesService
  with PullRequestService
  with WebHookIssueCommentService
  with IssueCreationService
  with gitbucket.core.util.WritableUsersAuthenticator
  with OwnerAuthenticator
  with ReadableUsersAuthenticator with ReferrerAuthenticator
  with IssueAcess
  with AccessService
  with RepositoryService with AccountService with ActivityService with WebHookService with CommitsService

trait HelloWorldControllerBase extends ControllerBase {
    self: RepositoryService with AccountService with ActivityService with WebHookService with CommitsService
    with AccountManagementControllerBase
    with IssueAcess
    with OwnerAuthenticator
    with AccessService
    with ReadableUsersAuthenticator with ReferrerAuthenticator =>
  val prefix = "@"

  // get("""^/([^/]+)/([^/]+).*""".r) {
  //   val params = multiParams("captures")
  //   //val owner = params("owner")
  //   //val repository = params("repository")
  //   val owner = params(0)
  //   val repository = params(1)
  //   println(request.getRequestURI())
  //   if (owner == "git")
  //     println("git でアクセスされました")
  //   else if (allReservedNames.contains(owner)) {
  //     println("使用できない文字を含んでいます")
  //     pass()
  //   }
  //   else {
  //     print(params)
  //     //pass()
      
  //     val repo = getRepository(owner, repository)
  //     val isRepository = repo.isDefined
  //     val msg = request.getRequestURI() +
  //     (if (isRepository)
  //       " ○レポジトリ" 
  //       else 
  //       "✗")
  //     println(msg)
  //     val result = referrersOnly { r =>
  //       //repository: RepositoryInfo => {
  //       val isAccessableUser = true;
  //       if (!isAccessableUser) {
  //           halt(403)
  //         }
  //         //println("プラグイン")
  //       }
  //       //println(result)
  //       pass()
  //     }
  //   //}
  //  }
  //   referrersOnly {
  //     repository: RepositoryInfo => {
  //       //repository.
  //       print(repository.name)
  //       pass()
  //     }
  //   }
  // )

  def loginUserIsIssueAccount() : Boolean = {
    val owner = request.paths(0)
    val repository = request.paths(1)
    val loginAccount = context.loginAccount
    return loginAccount.nonEmpty && isIssueUser(owner, repository, loginAccount.get.userName)
  }

  post("/:owner/:repository/_preview") {
    if (!loginUserIsIssueAccount()) {
      pass()
    } else {
      val preview = new MyPreview {}
      preview.handle(request, response)
      println("preview")
    }
  }

  get("""^/([^/]+)/([^/]+)/(issues(|/[0-9]+(/.*)?|/new|/batchedit/.*|/edit_title/[0-9]+|/edit/[0-9]+/|/_data/[0-9]+)|issue_comments/.+|_attached/.+)$""".r) {
    if (!loginUserIsIssueAccount()) {
      pass()
    } else {
      val hoge = new Hoge()
      hoge.handle(request, response)
      println("issues GET")
    }
  }
  post("""^/([^/]+)/([^/]+)/(issues(|/[0-9]+(/.*)?|/new|/batchedit/.*|/edit_title/[0-9]+|/edit/[0-9]+|/_data/[0-9]+)|issue_comments/.+|_attached/.+)$""".r) {
    if (!loginUserIsIssueAccount()) {
      pass()
    } else {
      val hoge = new Hoge()
      hoge.handle(request, response)
      println("issues POST")
    }
  }

  get("/:owner/:repository/settings/access") {
      ownerOnly { repository => {
        val issueUsers = getIssueUsers(repository.owner, repository.name)
        .map(x => (new Collaborator(repository.owner, repository.name, x._1, ""), x._2))
        // println(issueUsers + "")
        // println(issueUsers.length)
        gitbucket.access.html.options(issueUsers, repository)
      }
    }
  }

  post("/:owner/:repository/settings/access") (ownerOnly { repository =>
    val issueUsers = params("collaborators").split(",")
    .withFilter(_.nonEmpty).map(x => x.split(":")(0))
    setIssueUser(repository.owner, repository.name, issueUsers)
    println(issueUsers)
    redirect(s"/${repository.owner}/${repository.name}/settings/access")
  })
}