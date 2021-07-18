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
import gitbucket.core.plugin.GitRepositoryFilter
import gitbucket.core.model.Session

class HelloWorldGitController extends HelloWorldGitControllerBase
  with AccountManagementControllerBase
  with GitRepositoryFilter
  with RepositoryService with AccountService with ActivityService with WebHookService with CommitsService
  with ReadableUsersAuthenticator with ReferrerAuthenticator

trait HelloWorldGitControllerBase extends ControllerBase {
    self: RepositoryService with AccountService with ActivityService with WebHookService with CommitsService
    with AccountManagementControllerBase
    with GitRepositoryFilter
    with ReadableUsersAuthenticator with ReferrerAuthenticator =>
  val prefix = "@"

    def filter(path: String, userName: Option[String], settings: SystemSettingsService.SystemSettings, isUpdating: Boolean)(
    implicit session: Session
  ): Boolean = {
      println("gitリポジトリにアクセスがありました " + path + " " + userName)
      return true
  }
}
