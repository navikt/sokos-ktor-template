const { getInput, setOutput, github, context } = require('@actions/github');

async function run() {
    const branchName = context.payload.ref.split('/').pop();
    const labelPrefix = branchName.split('/')[0]; // Get the first part (e.g., 'feature')
    const label = labelPrefix; // Use as label directly

    // Add the label to the pull request
    const prNumber = context.payload.pull_request ? context.payload.pull_request.number : null;

    if (prNumber) {
        await github.issues.addLabels({
            owner: context.repo.owner,
            repo: context.repo.repo,
            issue_number: prNumber,
            labels: [label],
        });
        console.log(`Label '${label}' added to PR #${prNumber}`);
    } else {
        console.log('No pull request associated with this push.');
    }
}

run().catch(error => {
    console.error(error);
    process.exit(1);
});
