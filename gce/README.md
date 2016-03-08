# Google Cloud Endpoints Client Library Generation

This directory holds things needed for generating Retrofit interfaces for our Google Cloud Endpoints Client Library.

Inside this directory is a modified version of [gce2retrofit](https://github.com/chiuki/gce2retrofit) that adds in an `Authorization` header to every possible method. This allows us to pass oauth tokens to the API so that we can use authenticated endpoints. Code changes can be found on [this fork](https://github.com/phil-lopreiato/gce2retrofit/tree/gce-auth)

A copy of the API's REST discovery document also is in this directory. This is used to genereate the retrofit service

## Git Large File Storage
Since the `gce2retrofit` jar is pretty big, we store it using [GitHub Large File Storage](https://help.github.com/articles/versioning-large-files/). In order to work with the jar, you'll need to [install git-lfs](https://help.github.com/articles/installing-git-large-file-storage/) and run `git lfs fetch` to populate your working copy.
