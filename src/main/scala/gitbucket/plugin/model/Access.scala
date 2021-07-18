package gitbucket.plugin.model

trait IssueAccessComponent {  self: gitbucket.core.model.Profile =>
   import profile.api._

  lazy val IssueAccesses = TableQuery[IssueAccesses]

  class IssueAccesses(tag: Tag) extends Table[IssueAccess](tag, "ISSUE_ACCESSES") {
    val userName = column[String]("USER_NAME")
    val repositoryName = column[String]("REPOSITORY_NAME")
    val issueUserName = column[String]("ISSUE_USER_NAME")
    def * = (userName, repositoryName, issueUserName) <> (IssueAccess.tupled, IssueAccess.unapply)
  }
}

case class IssueAccess(
  userName: String,
  repositoryName: String,
  issueUserName: String
)