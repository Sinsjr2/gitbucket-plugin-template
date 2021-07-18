import io.github.gitbucket.helloworld.controller.HelloWorldController
import io.github.gitbucket.solidbase.model.Version
import gitbucket.core.service.RepositoryService.RepositoryInfo
import gitbucket.core.plugin.GitRepositoryRouting
import io.github.gitbucket.helloworld.controller.HelloWorldGitController
import gitbucket.core.service.RepositoryService
import gitbucket.core.plugin.Link
import io.github.gitbucket.solidbase.migration.LiquibaseMigration

class Plugin extends gitbucket.core.plugin.Plugin {
  override val pluginId: String = "helloworld"
  override val pluginName: String = "HelloWorld Plugin"
  override val description: String = "First example of GitBucket plug-in"
  override val versions: List[Version] = List(
    new Version("1.0.0", new LiquibaseMigration("update/gitbucket-access_1.0.0.xml"))
  )

   override val controllers = Seq(
    "/*" -> new HelloWorldController()
  )

    override val repositorySettingTabs =
      Seq((repository, context) => Some(Link("access", "Access", s"settings/access")))
}
