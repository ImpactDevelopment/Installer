# Contributing to the Installer

## I have a question
Issues aren't intended for handling questions. You're much better off contacting us through one of these methods [Discord]()

## I found a bug
Before reporting a bug, please take the time to check if someone else has already. Search the [issues list] for keywords that might match your issue.

If you can't find the bug already reported, we'd greatly appreciate it if you report it to us. Click the [new issue] button and select "bug". Then try and follow the template to provide us with useful information in fixing the issue.

## I have an idea
That's great! Similar to bugs, it's possible someone else has already reported it, so please check the [issues list] to see if you can find anything matching your idea.

If not, we welcome any suggestion, but please don't be offended if we don't think it'll fit well with the project. Click the [new issue] button, choose "enhancement" and try to follow the template to provide us with useful information.

## I want to fix a bug or implement an idea
Brilliant. You'll first want to ensure you have [forked us][fork], cloned your fork and [setup a development environment][devenv].

Please do your best to follow the guidance below regarding [documenting your changes]. Also note that we require all commits to be signed by a verified PGP key. Be sure to add yourself to the [CONTRIBUTORS][contrib] file too.

Once that's done you can checkout a new branch, make your changes to it, publish it and open a Pull Request asking us to merge the changes in your branch.

Please see  [Creating a Pull Request from a Fork](https://help.github.com/en/articles/creating-a-pull-request-from-a-fork). If you're brand new to git checkout [Try Git](http://try.github.io/) or one of these [awesome tutorials](https://gist.github.com/jaseemabid/1321592)

### Documenting your changes

There are four main aspects to documenting your changes; in-code comments, Javadoc, commit messages and changelog entries.

- Comments: these exist to aid reading the code, they should explain the intent of a particular bit of code, comments shouldn't simply repeat what the code says in english.
- Javadoc: this exists to explain what a particular method does or what a field is for, without the need to read the method's code or examine usages of the field.
- Commit messages: these exist to explain a change to the code, rather than simply repeating what the change is they should strive to concisely capture the intent behind a change. See [How to write a git commit message][good commit message] by Chris Beams.
- Changelog: this exists to allow end users to get a feel of what functionally changed in a given release. It should cover features being added/changed/removed and bugs being fixed, but not _how_ the changes were implemented. Try and write a changelog so that your grandparents can read it! Take a look at [Keep a Changelog] to get a feel for how a Changelog should work  

### Updating the Changelog
If you change how the installer behaves, you should update the changelog. Generally, we like the changelog to be updated from the same commit that mae the change. When you open `CHANGELOG.md` you'll notice that it is structured as per [Keep a Changelog], with a heading for each version and sub-headings for types of changes.

You'll want to add your change in the `## [Upcoming]` section, since it will most likely make it into the next release rather than any previous release. You will also need to choose a sub-heading and check to see if it exists yet, if not add it yourself. Keep a Changelog recommend the following sub-headings:

- `Added` for new features.
- `Changed` for changes in existing functionality.
- `Deprecated` for soon-to-be removed features.
- `Removed` for now removed features.
- `Fixed` for any bug fixes.
- `Security` in case of vulnerabilities.

Finally, remember that Changelogs are for humans, not machines. You should be describing what changed, from a user's perspective, not how it was changed from a technical perspective (that is what commit messages are for).  

[issues list]: /ImpactDevelopment/Installer/issues
[new issue]: /ImpactDevelopment/Installer/issues/new
[fork]: /ImpactDevelopment/Installer/fork
[devenv]: /ImpactDevelopment/Installer#setting-up-a-development-environment
[contrib]: CONTRIBUTORS.md
[documenting your changes]: #documenting-your-changes
[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
[good commit message]: https://chris.beams.io/posts/git-commit/