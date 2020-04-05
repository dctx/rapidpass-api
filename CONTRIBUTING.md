# How to contribute

The RapidPass API is a team effort. This document details how we can work together as a team.

If you are unsure of whether your contribution should be implemented as a
module or part of RapidPass, you may visit [#rapidpass-backend on slack](https://dctx.slack.com/).

## Getting Started

* Make sure you have a [GitLab account](https://www.gitlab.com).
* Sign up as a volunteer at [this link](https://bit.ly/SignUp_DevsDCTX).
* Join the `#rapidpass-backend` Slack channel.
  
You can read in more detail how to get started (setting up your local environment, using docker, etc) from our [quickstart guide](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/wikis/guide/Quickstart-Guide). 

## Making Changes

* New git branches should be created based on the filed GitLab [issues](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues).  
* If you are filing an issue, make sure you follow the [issue template](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/wikis/templates/Issue-Template).
* Make sure that your commits are logical and atomic units.
* Make sure you have added the necessary tests for your changes.
* For details on how to run tests, please see [the quickstart guide](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/wikis/guide/Quickstart-Guide).

## Submitting Changes

When you're ready to start submitting changes, follow these instructions:

1. Create a Merge Request (MR) from a specific GitLab issue. By creating an MR from an 
   issue, This should create a new branch for you to commit your work on.  
2. Name the MR beginning with WIP to signify that it is not yet ready to be merged.
3. Check out the newly created MR branch.
4. Apply your changes, bug fixes, or new features on your local copy of the branch.
5. The change you are introducing must have unit tests demonstrating the 
  correctness of the change.
6. Run all the unit tests locally to ensure that all the unit tests are passing.
7. Bump the project version as specified in `pom.xml` according to [Semantic Versioning](https://semver.org/).
8. Update the CHANGELOG.md file in the root project folder.
9. Commit your changes to your local MR branch.
10. Push your branch to origin.
11. Rename your MR to remove the WIP, signifying that it is ready to be merged. Apply the `Review` label on the MR. 
12. Message in the [#rapidpass-backend on slack](https://dctx.slack.com/) informing the maintainers that it is ready for review.
13. Maintainers will perform the code review and will merge the MR when it is satisfactory.

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

* [DCTx community guidelines](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/wikis/guide/Community-Guidelines)
* [Issue tracker](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues)
* [Issue template](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/wikis/templates/Issue-Template)
* [DCTx Slack](https://dctx.slack.com)

> This contributing guidelines is based on [puppet's contributing guidelines](https://github.com/puppetlabs/puppet/blob/master/CONTRIBUTING.md).