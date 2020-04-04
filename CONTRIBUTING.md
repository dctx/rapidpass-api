# How to contribute

The RapidPass API is a team effort. This document details how we can work together as a team.

## RapidPass Core vs Modules

If you are unsure of whether your contribution should be implemented as a
module or part of RapidPass, you may visit [#rapidpass-backend on slack](https://dctx.slack.com/).

## Getting Started

* Make sure you have a [GitLab account](https://www.gitlab.com).
* Sign up as a volunteer at [this link](https://bit.ly/SignUp_DevsDCTX).
* Join the `#rapidpass-backend` Slack channel.
* Submit a GitLab ticket for your issue if one does not already exist.
  * Clearly describe the issue including steps to reproduce when it is a bug.
  * Make sure you fill in the earliest version that you know has the issue.
* Fork the repository from GitLab.

## Making Changes

* Create a topic branch from where you want to base your work.
  * This is usually the `develop` branch.
  * Only target release branches if you are certain your fix must be on that
    branch.
  * You can do this using `git flow feature start NAME_OF_FEATURE`. 
* Make commits of logical and atomic units.
* Check for unnecessary whitespace with `git diff --check` before committing.

* Follow the [commit template]() (Unclear here. TBD).

* Make sure you have added the necessary tests for your changes.
* For details on how to run tests, please see [the quickstart guide]()

## Submitting Changes

* Sign the [Contributor License Agreement](https://www.google.com).
* We use `git flow` as a standard for managing our git branches. 
* Push your changes to a `feature`, or `bugfix`, or whichever branch in your fork of the repository.
* Submit a merge request to the repository.
* Update the related issue to mark that you have submitted code and are ready
  for it to be reviewed (Status: Ready for Merge).
* The core team looks at pull requests on a regular basis.
* After feedback has been given, we expect responses within two weeks. After two
  weeks we may close the pull request if it isn't showing any activity.

## Revert Policy

By running tests in advance and by engaging with peer review for prospective
changes, your contributions have a high probability of becoming long lived
parts of the the project. After being merged, the code will run through a
series of testing pipelines on a large number of operating system
environments. These pipelines can reveal incompatibilities that are difficult
to detect in advance.

If the code change results in a test failure, we will make our best effort to
correct the error. If a fix cannot be determined and committed within 24 hours
of its discovery, the commit(s) responsible _may_ be reverted, at the
discretion of the committer and the project maintainers. This action would be taken
to help maintain passing states in our testing pipelines.

The original contributor will be notified of the revert in the ticket
associated with the change. A reference to the test(s) and operating system(s)
that failed as a result of the code change will also be added to the
ticket. This test(s) should be used to check future submissions of the code to
ensure the issue has been resolved.

### Summary

* Changes resulting in test pipeline failures will be reverted if they cannot
  be resolved within one business day.

## Additional Resources

* [DCTx community guidelines](https://www.google.com/)
* [Issue tracker](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues)
* [Contributor License Agreement](https://www.google.com/)
* [Issue template](https://www.google.com/)
* [DCTx Slack](https://dctx.slack.com)

> This contributing guidelines is based on [puppet's contributing guidelines](https://github.com/puppetlabs/puppet/blob/master/CONTRIBUTING.md).