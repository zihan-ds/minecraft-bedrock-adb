# Usage: .\scripts\release.ps1 1.2.2
# Updates the mod version in gradle.properties AND fabric.mod.json,
# then commits and creates tag v<VERSION>.
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

$fabricJson = Get-Content 'src/main/resources/fabric.mod.json' -Raw
$fabricJson = $fabricJson -replace '"version":\s*"[^"]*"', "`"version`": `"$Version`""
Set-Content 'src/main/resources/fabric.mod.json' -Value $fabricJson -NoNewline -Encoding utf8

git add gradle.properties src/main/resources/fabric.mod.json
git commit -m "chore: release v$tagVersion"
git tag "v$tagVersion"

Write-Host "Created commit and tag v$tagVersion (version $Version)"
Write-Host "Push with: git push && git push --tags"
