# Usage: .\scripts\release.ps1 1.2.2
# Updates the mod version, commits and creates tag v<VERSION>.
param(
    [Parameter(Mandatory = $true)]
    [string]$Version
)

if ($Version -notmatch '^\d+\.\d+\.\d+\+\d+(\.\d+)?$') {
    Write-Error "Version should look like 1.2.2+1.21"
    exit 1
}

$tagVersion = ($Version -split '\+')[0]
$props = Get-Content 'gradle.properties' -Raw
$props = $props -replace '^mod_version=.*$', "mod_version=$Version"
Set-Content 'gradle.properties' -Value $props -NoNewline -Encoding utf8

git add gradle.properties
git commit -m "chore: release v$tagVersion"
git tag "v$tagVersion"

Write-Host "Created commit and tag v$tagVersion (version $Version)"
Write-Host "Push with: git push && git push --tags"
