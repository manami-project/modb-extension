[![Tests](https://github.com/manami-project/modb-extension/actions/workflows/tests.yml/badge.svg?branch=main)](https://github.com/manami-project/modb-extension/actions/workflows/tests.yml) ![jdk21](https://img.shields.io/badge/jdk-21-informational)
# modb-extension

> [!NOTE]
> **This repository is experimental** \
> Significant and breaking changes can potentially be made to both code and structure. I am also not sure whether I will maintain this project continuously or leave it as a public archive for demonstration purposes.

_[modb](https://github.com/manami-project?tab=repositories&q=modb&type=source)_ stands for _**M**anami **O**ffline **D**ata**B**ase_ and basically represents [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database) which is a dataset specifically created for [manami](https://github.com/manami-project/manami).
This project is both an experiment and a demonstration on how to extend the existing project.

## What is the purpose of this repository?

The [anime-offline-database](https://github.com/manami-project/anime-offline-database) dataset has specifically been created for [manami](https://github.com/manami-project/manami). I've been asked a lot to add other properties.
However the dataset meets its needs and fulfills its purpose. Additional properties increase the datasets size and create a lot more work for me.
That's why I want to keep the dataset as-is.

I've been asked to add synopsis a few times. The problems I see with synopsis are:
* Synopsis cannot be merged. You either have to add each text from each meta data provider which is completely against the idea of the dataset or you'd have to select a single text each time.
* They are not public domain. It's a creatively written text by someone. So in my understanding those are very likely protected by copyright.

That got me curious and thinking how this could possibly be solved and if I could easily extend my current dataset.
With this repository I want to see if I can create an extension to the [anime-offline-database](https://github.com/manami-project/anime-offline-database) dataset.

## Content of the repo

See also the [README.md](tooling/lib/README.md) file of the lib to learn how to access the data.

### /data

This is a folder containing the data as separate JSON files.

### /tooling

A kotlin library which lets you access the data. It is best used in conjunction with the other modb libraries. The folder also contains an app which creates the data in `/data`.
See the `README.md` files in the subfolders for more information.

## Available data

### Synopsis

My initial idea was to hand over existing synopsis to a large language model (LLM), let it understand the content of the anime only from these texts and create its own synopsis.
From my perspective this would actually be a merge process and it would scale.
Apart from cleaning up the data so that the model doesn't get confused, the app only generates a synopsis if an anime has at least 3 distinct synopsis each having at least 20 words.
Otherwise I'm afraid that the LLM just copies text or the text would be too close to the original.

When using mistral LLM almost all of the synopsis started with `In the...`. Sometimes variations like `In a...`. It was not possible to get rid of this just by editing the prompt itself.
That's how I learned that LLMs tend to ignore parts of the prompt. To change that behavior I had to extract a word which already appeared as the beginning of a sentence in one of the originals and set it as the starting point.
This lead to more varied openings, but also to complex code and some weird edge case outputs. I finally fixed this by changing the LLM to anthropic with a little prompt tweak.

### Score

Each meta data provider offers a score, but each score is different.
First you have to identify the desired score which is an arithmetic mean. Some sites also offer a weighted score, but the wording is not always clear.
After that you have to scale the values, because there are different rating systems:
* 1-10
* 1-5
* 1-100

Even if multiple sites have a 1-5 rating, the lowest rating can differ from `1.0` over `0.5` up to `0.1`.
I decided to use a 1-10 rating. For each anime I rescale the original values and then calculate:
* arithmetic mean
* arithmetic-geometric-mean
* median
