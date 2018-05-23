#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_da73d47e8be3_key -iv $encrypted_da73d47e8be3_iv -in cd/codesigning.asc.enc -out cd/codesigning.asc	-d
    gpg --fast-import cd/codesigning.asc
fi