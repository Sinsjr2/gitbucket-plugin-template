package gitbucket.plugin.model

import gitbucket.core.model._
object Profile extends ProfileProvider
    with Profile
    with AccountComponent
    with GroupMemberComponent
    with IssueAccessComponent