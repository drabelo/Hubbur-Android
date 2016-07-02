# Hubbur for Android

(c) 2015

---

### Workflow

Do not use the Android Studio version control tools; everything should be done
from the command line.

Clone the repo to get started:

`$ git clone git@bitbucket.org:hubbur/hubbur-android.git`

Make sure you have SSH keys set up to push to the remote repo. A tutorial can be
found [here](https://confluence.atlassian.com/display/BITBUCKET/Set+up+SSH+for+Git).

The `master` branch should be considered our "stable" branch; it should never be
broken. We use feature branches to help achieve this:

1. Once you're ready to work and have the latest version of the code (`git pull`),
check out a feature branch with your username and feature name to get going:
    * For example: `git checkout -b tylucaskelley/real-time-messaging master`
2. Work on the new feature, committing your changes as you go.
3. Once you're ready to go, go back to the master branch:
    * `git checkout master && git pull`
4. Attempt to merge your changes:
    * `git merge --no-ff <your-branch-name-goes-here>`
5. If there were any merge conflicts resolve them (a.k.a. rinse and repeat)
6. Push your changes (from the master branch)
    * `git push origin master`

NOTE: once we have automated tests and a CI server set up, this doc will be updated. Nobody will be allowed to push unless all tests pass, etc.
