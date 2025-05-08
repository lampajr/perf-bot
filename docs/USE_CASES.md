# Interactions

## Run test
The following sequence diagram shows an example of interactions from the PR creation to
the performance job result posting to the same PR.

```mermaid
sequenceDiagram
    User->>GitHub: Open Pull Request
    User->>GitHub: Add comment "/perf run test"
    GitHub-->>PerfShiftLeftService: Send IssueComment event
    PerfShiftLeftService->>+PerfShiftLeftService: Unmarshall the GitHub event
    PerfShiftLeftService-->>+PerfLab: Trigger performance job
    PerfShiftLeftService-->>-PerfShiftLeftService: Store issue that triggered the job
    PerfLab->>+Horreum: Upload perf job results
    Horreum->>PerfShiftLeftService: Send uploaded run
    Horreum-->>-PerfLab: Confirm run upload
    PerfLab-->>-PerfLab: Mark job as completed
    PerfShiftLeftService-->>PerfShiftLeftService: Retrieve original issue
    PerfShiftLeftService-->>GitHub: Post job results to the Pull Request
```

**Actors**

* **PerfShiftLeftService**: GitHub App that manages the interaction with GitHub itself.
Its main objective is to interpret the GitHub events and properly trigger the corresponding
performance job. After that it implements a Horreum webhook to get notified when the results
are uploaded so that it can post the results back to the original pull request.
* **PerfLab**: Where the actual performance jobs will run, initially this could be a Jenkins instance
* **Horreum**: Here Horreum is used as backend datastore for performance jobs results

## Compare runs

TBD