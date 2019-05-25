# Maintaining Impact Installer

This guide is intended to enable current and future maintainers to maintain and releases Impact Installer in a consistent manner. It should be updated as processes change.

## Merging PRs
GitHub's web UI supports three methods of merging PRs:

- _Merge_ (`git merge --no-ff`) will always produce a merge commit, whether one is required or not.
- _Rebase_ (git rebase master && git checkout master && git merge --ff feature`)
- _Squash_ (git merge --squash feature && git commit -m "feature (#1)"`)

The problem with the second two is that any signatures will be replaced with GitHub's signature. The problem with the first is that a merge commit will always be created, even if it is not required.

One alternative is to ask the contribute to re-write their history and force push to their PR branch. If the branch history is messy, this may be the only option that maintains their signature.

If the branch history is already neat enough for `master`, you could also use `git merge` locally. This will only create a merge commit if one is required. If one is created you may wish to reword the merge commit to something along the lines of `Merge PR #1 "Added stuff and things"` (this can be done by passing `-m` to `git merge` or by using `git commit --amend` after merging).
If a merge commit will be crated, you may consider using GitHub's merge button, noting that it will be GitHub's signature not your's on the merge commit.

Finally, if you are unconcerned about maintaining the user's signature, you have a few more options: If you are happy with GitHub's signature being used, then their `Squash` or `Rebase` merge buttons will work fine. Alternatively if you'd rather your signature be on the commits then you can do the respective equivalents locally. 

## Releasing
Before releasing the version number should be bumped in-line with semantic versioning.
The changelog will also need to be updated as described below. These changes should then be committed locally and the artifacts should be built by running `./gradlew build createExe`.

Due to branch protection, you will have to push your release commit to a branch, have another maintainer review the changes before merging into `master`.
At this point you can tag the release commit with the matching version number (`git tag 1.0.1 && git push origin 1.0.1` for example). Once the tag is pushed you can create the release using [GitHub's process](https://help.github.com/en/articles/creating-releases) being sure to upload the build artifacts you built earlier (from `build/lib` and `build/launch4j`).

Once happy, choose `Publish release`.

Note: it is also possible to use the [GitHub API to create a release](https://developer.github.com/v3/repos/releases/#create-a-release), we may wish to do this from CI or from a local release script to ease the process in the future.

## Versioning

Impact Installer uses [semantic versioning](https://semver.org/spec/v2.0.0.html). This specifies when each component of the version should be incremented.

Semantic versioning labels the version number as `Major.Minor.Patch`.

- `Patch` MUST be incremented if only backwards compatible bug fixes are introduced. A bug fix is defined as an internal change that fixes incorrect behavior.
- `Minor` MUST be incremented if new, backwards compatible functionality is introduced. It MUST be incremented if any functionality is marked as deprecated. It MAY be incremented if substantial new functionality or improvements are introduced within the private code. It MAY include `Patch` level changes.
- `Major` MUST be incremented if any backwards incompatible changes are introduced. It MAY include `Minor` and `Patch` level changes.

In simple terms, `Patch` is for bugfixes, `Minor` is for new features (and deprecating old features) and `Major` is for breaking changes.

Since the installer isn't really an API per-se, we should consider our "API" to be our GUI and CLI interfaces.
If we add new features to either, we bump `Minor`. If we change/remove a feature in a breaking way we should bump `Major`.
We should endeavour to deprecate CLI features in a `Minor` version before removing them in a later `Major` version, instead of abruptly removing them without warning.

Versions in the initial development phase should use a `0` `Major` version to indicate the interface is liable to sudden change without warning. `Minor` and `Patch` should be used as normal, starting from version `0.1.0`.

## Changelog

Each version released should have its own heading in the Changelog. This heading should be in the format of `## [version number] - date`. The `[version number]` should link to the reagent GitHub release.

Unreleased changes should be added under an `## [Unreleased]` heading. `[Unreleased]` should link to the diff between the latest release and `HEAD`, e.g. `https://github.com/ImpactDevelopment/Installer/compare/0.1.0...HEAD`. No date is appended.

On release, the `## [Unreleased]` heading should be replaced with a heading for the new release. There is no need to create a new `[Unreleased]` heading until new unreleased changes are committed.