package gitbucket.plugin.service
import gitbucket.plugin.model.{IssueAccess}
import gitbucket.plugin.model.Profile._
import gitbucket.plugin.model.Profile.profile.blockingApi._

import gitbucket.plugin.model._
import io.github.gitbucket.helloworld.controller.IssueAcess

trait AccessService {

   private def byRepository(x:IssueAccesses, userName: String, repositoryName: String) =
      (x.userName === userName.bind) && (x.repositoryName === repositoryName.bind)

   /**
   * issue にアクセスできるユーザーをすべて削除します。
   */
   private  def removeIssueUser(userName: String, repositoryName: String)(implicit s: Session): Unit  = {
      IssueAccesses.filter(x => byRepository(x, userName, repositoryName))
      .delete
  }

  /**
   * issue にアクセスできるユーザーを追加します。
   */
   private def addIssueUsers(userName: String, repositoryName: String, issueUsers: Seq[String])(implicit s: Session): Unit = {
      IssueAccesses.insertAll(issueUsers.map(issueUserName => IssueAccess(userName, repositoryName, issueUserName)): _*)
   }

   /**
     * issueにアクセスできるユーザーを設定します。
     */
   def setIssueUser(userName: String, repositoryName: String, issueUsers: Seq[String])(implicit s: Session): Unit = {
      removeIssueUser(userName, repositoryName)
      addIssueUsers(userName, repositoryName, issueUsers)
   }

   /**
     * 指定したアカウントがリポジトリに設定されているissueに接続可能なユーザーもしくはアクセス可能なグループに
     * 属しているかを判定します。
     */
   def isIssueUser(userName: String, repositoryName: String, loginUser: String)(implicit s: Session): Boolean = {
      val issueUser = IssueAccesses
         .filter(x => byRepository(x, userName, repositoryName) && x.issueUserName === loginUser.bind)
         .map(x => true)
      
      val issueGroup = IssueAccesses.filter(x => byRepository(x, userName, repositoryName))
         .join(Accounts)
         .on { case (t1, t2) => (t1.issueUserName === t2.userName) && (t2.groupAccount === true.bind)}
         .join(GroupMembers)
         .on { case ((t1, t2), t3) => t2.userName === t3.groupName && t3.userName === loginUser.bind }
         .map(x => true)

      // ユーザー もしくは グループに含まれているか
      issueUser.union(issueGroup)
      .list
      .nonEmpty
   }

   /**
     * リポジトリに対してissueにアクセスできるユーザーの一覧を取得します。
     *  ユーザー名もしくはグループ名を返します。
     * グループ名のときはtrueを返します。
     */
   def getIssueUsers(userName: String, repositoryName: String)(implicit s: Session): List[(String, Boolean)] = {
    val q1 = IssueAccesses
      .join(Accounts)
      .on { case (t1, t2) => (t1.issueUserName === t2.userName) && (t2.groupAccount === false.bind) }
      .filter { case (t1, t2) => byRepository(t1, userName, repositoryName) }
      .map { case (t1, t2) => (t1.issueUserName, false) }

    val q2 = IssueAccesses
      .join(Accounts)
      .on { case (t1, t2) => (t1.issueUserName === t2.userName) && (t2.groupAccount === true.bind) }
      .join(GroupMembers)
      .on { case ((t1, t2), t3) => t2.userName === t3.groupName }
      .filter { case ((t1, t2), t3) => byRepository(t1, userName, repositoryName) }
      .map { case ((t1, t2), t3) => (t1.issueUserName, true) }

      q1.union(q2)
      .list
   }
}