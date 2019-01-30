[commit-conventions]: https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit?usp=sharing
[git-interactive-rebase]: https://robots.thoughtbot.com/git-interactive-rebase-squash-amend-rewriting-history
[suit-css]: https://suitcss.github.io
[suit-css-styleguide]: https://github.com/suitcss/suit/blob/master/doc/STYLE.md#41-comments

# Contributing guidelines

> General guidelines on how to effectively contribute to the project

<details>
  <summary>Table of contents</summary>
  <ul>
    <li><a href="#definition-done">Definition of Done</a></li>
    <li><a href="#task-types">Types</a></li>
    <li><a href="#git-workflow">Git workflow</a></li>
    <ul>
      <li><a href="#git-workflow-general-rules">General rules</a></li>
      <li><a href="#branch-naming">Branch naming</a></li>
      <li><a href="#branch-workflow">Branch workflow</a></li>
    </ul>
    <li><a href="#commit-messages">Commit messages</a></li>
    <ul>
      <li><a href="#commit-messages-general-rules">General rules</a></li>
      <li><a href="#commit-messages-format">Format</a></li>
      <li><a href="#commit-messages-example">Example</a></li>
    </ul>
    <li><a href="#code-review">Code review</a></li>
  </ul>
</details>

## <a name="definition-done"></a>Definition of Done
* Javadoc para classes e métodos
* Testes unitários
* Teste de regressão e Deploy com sucesso:
    ```mvn clean install```
* Changelog quando aplicável
* Código aprovado para a develop

## <a name="task-types"></a>Types to use on branchs, commits and tasks.

| Type     | Description                                               |
| -------- | --------------------------------------------------------- |
| feat     | A new feature                                             |
| fix      | A bug fix                                                 |
| docs     | Documentation only changes                                |
| style    | Changes that do not affect the meaning of the code        |
| refactor | A code change that neither fixes a bug or adds a feature  |
| perf     | A code change that improves performance                   |
| test     | Adding missing tests                                      |
| chore    | Changes to the build process or auxiliary tools/libraries |
| api      | Adding or changing mock api data                          |

## <a name="git-workflow"></a>Git workflow

```sh
$ # Make sure you're on the `master` branch
$ git checkout master
$ # Sync with remote (rebase, just in case)
$ git pull --rebase
$ # Start off your feature branch from `master`
$ git checkout feat/sidebar
$ # Profit
```

### <a name="git-workflow-general-rules"></a>General rules

* **Do not merge**, use `rebase` instead
* Keep the history log meaningful and clean, use [interactive rebase][git-interactive-rebase] to help you with that
* Feature branches shouldn't live long and have to contain a very specific set of changes, keep it short and concise

### <a name="branch-naming"></a>Branch naming

Branch naming should be a combination of type and title:

* `feat/content-list`
* `feat/footer`
* `fix/fout`
* `fix/analytics`
* `chore/eslint`
* `docs/api`

### <a name="branch-workflow"></a>Branch workflow

1. Create a new feature branch from `master`
2. Do your thing
3. Keep your feature branch up to date by rebasing it against `master`
4. Once ready to submit to code review, create a pull request pointing to `master`
5. After code review is done, merge to `master`


## <a name="commit-messages"></a>Commit messages

Commit messages are also documentation of our code, so clear and descriptive commit messages can be a really powerful tool.

We adopt the [AngularJS Git Commit Message Conventions][commit-conventions].

### <a name="commit-messages-general-rules"></a>General rules

* Wrap the body of the message at `72` characters
* Add a blank line between the header and the body of the message
* Use the imperative, present tense ("change" not "changed" nor "changes")
* Always use lowercase for the commit message summary (even for abbreviations)
* Do not finish the subject with a period

### <a name="commit-messages-format"></a>Format

```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

### <a name="commit-messages-example"></a>Example

A great commit message should look like this:

```
docs(instructions): update api reference

Update API reference for new endpoints:

- `/location`: get user location
- `/recommendation`: get a list of recommendation based on user input

Closes #42 and #55
```


## <a name="code-review"></a>Code review

1. Create a pull request on GitHub pointing to your feature branch
2. When naming your pull request, **choose a human readable title** (no need to follow the commit message standard)
3. When describing the changes you're introducing, be **very descriptive** and feel free to use images to help reviewers understand
4. Assign your pull request to **at least two** peers (no need to ping them on Slack, really)
5. When replying to feedbacks from your peers, make sure you mention them using `@`, so they get notified
6. When giving an OK to a pull request, choose between LGTM, :rocket: or :ship: (pick one of these options only)
7. Once you have **at least two** OKs you are free to **squash** back to `master`
8. Remove your feature branch from the remote repository