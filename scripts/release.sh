#!/usr/bin/env bash
# Usage: ./scripts/release.sh 1.2.2
# Updates the mod version, commits and creates tag v<VERSION>.
set -euo pipefail

VERSION="${1:?Usage: release.sh <version>}"

# Sanity check against version scheme {mod_version}+{mc_version}
if [[ ! "${VERSION}" =~ ^[0-9]+\.[0-9]+\.[0-9]+\+[0-9.]+$ ]]; then
  echo "Version should look like 1.2.2+1.21" >&2
  exit 1
fi

TAG_VERSION="${VERSION%%+*}"   # 1.2.2

sed -i "s/^mod_version=.*/mod_version=${VERSION}/" gradle.properties

git add gradle.properties
git commit -m "chore: release v${TAG_VERSION}"

git tag "v${TAG_VERSION}"

echo "Created commit and tag v${TAG_VERSION} (version ${VERSION})"
echo "Push with: git push && git push --tags"
