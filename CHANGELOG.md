# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

### [3.4.5](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v3.4.4...v3.4.5) (2022-10-11)

### [3.4.4](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v3.4.3...v3.4.4) (2022-10-05)


### Bug Fixes

* fix id property name ([73fcbb6](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/73fcbb61b86834dd4362c69cc3827fed82d9de04))

### [3.4.3](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v3.4.2...v3.4.3) (2022-10-05)


### Bug Fixes

* tag event type ([f759071](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/f759071b2fd1f03d40d76075e027fc1f1e562494))

### [3.4.2](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v3.4.1...v3.4.2) (2022-10-05)


### Bug Fixes

* run authorization check in transaction ([547d466](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/547d466314221b69c812aa87bcffa99ec3eafebf))

### [3.4.1](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v3.4.0...v3.4.1) (2022-10-05)


### Bug Fixes

* kafka message deserialization ([747b2cb](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/747b2cb5799831b0f9756a0bb38812b3631d24ca))
* make controllers transactional ([d944a5a](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/d944a5aa122fde1b96633f972ebfdf570ead4b83))

## [3.4.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v3.3.0...v3.4.0) (2022-10-02)


### Features

* sort by rank ([44af92c](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/44af92c9dfe938d471259395c315f9346b9e91f0))

## [3.3.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v3.2.0...v3.3.0) (2022-10-01)


### Features

* add tags to dto ([f14ccfd](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/f14ccfdea877b79dd1378aa9fc62ccd95dadf05b))


### Bug Fixes

* fix nullpointer ([a6b2ed4](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/a6b2ed414df2ce77a3d33acc3bf8bd33be874776))

## [3.2.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v3.1.0...v3.2.0) (2022-10-01)


### Features

* add simple tags to projects/proposals ([913de7d](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/913de7d5774abc34a5bea98b2122c0dee62fa89d))
* add tags for search capability ([dfa61f1](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/dfa61f15eeeec5830f9dbd0e33da453f99c1c5d2))


### Bug Fixes

* only fetch one bag ([7d08afb](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/7d08afba6fa2e030824199b06bf2a5a4ead6a16f))

## [3.1.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v3.0.0...v3.1.0) (2022-09-14)


### Features

* add status filter to proposal API ([a056468](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/a056468e50f6cccfdebf0cd9c914aa75a6db0e4a))

## [3.0.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.7.0...v3.0.0) (2022-09-14)


### ⚠ BREAKING CHANGES

* reference proposal in project
* use a new status instead deleting proposal

### Features

* add a back-reference to the created project ([4f2a38e](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/4f2a38eb4e319a3c1ca17d6e929121302c12ea54))
* add abstractions for events ([ca31316](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/ca31316d3016240fb482dcc10b67ccaadbf52272))
* add proposal id to project dto ([0e87605](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/0e87605a9a99d17a96ecae271d74cefd035705d8))
* reference proposal in project ([65f3be5](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/65f3be5546654d2edfb4e5b4bd5cbffaf17dcb87))
* use a new status instead deleting proposal ([dae5c99](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/dae5c998a932051bf7ee7d892cf72bcfd10736da))


### Bug Fixes

* make event listeners really synchronous ([afcd140](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/afcd1406bf5651722e257e379350fc11aa8884f2))

## [2.7.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.6.0...v2.7.0) (2022-09-12)


### Features

* give supervisors write-acess on projects ([2ee14bf](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/2ee14bf5eb9fcd6f755ea3e36286a2f077442d69))

## [2.6.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.5.0...v2.6.0) (2022-09-12)


### Features

* add commit permission to response ([ac564dd](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/ac564dd6580c5cf2970de43bfb70df3d68ec1588))

## [2.5.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.4.0...v2.5.0) (2022-09-12)


### Features

* add permissions to proposal DTO ([47a4024](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/47a4024bee1a3058632f1c4de6dc6ac21231cb27))

## [2.4.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.3.1...v2.4.0) (2022-09-10)


### Features

* publish event once a proposal is being promoted to a project ([930967b](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/930967bae8591753a46ac35c16b4dda48b3c87ec))

### [2.3.1](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.3.0...v2.3.1) (2022-09-03)


### Bug Fixes

* add requestbody annotations ([9147134](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/91471344dbf03510acd6cda56906c044f310dca4))

## [2.3.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.2.0...v2.3.0) (2022-09-03)


### Features

* add reconciliation apis for admins ([d0f93db](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/d0f93dbaa233eab852be208adee166323c64f035))
* consume members from organization event ([acc64a5](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/acc64a541463ddafeaf497ee57b4cd22e0952ccf))
* introduce commitment ([ead738f](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/ead738feb6bcb8eec7bd065a446b87cef2b86fe0))
* publish kafka events ([170be0b](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/170be0ba8cc56178d37b73960d43e244102bee7c))


### Bug Fixes

* fix configmap ([22d11e2](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/22d11e27d30a8ffa7247c2fab7c99eef2df63a2e))

## [2.2.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.1.1...v2.2.0) (2022-07-20)


### Features

* implement auto archive and deletion for proposals ([a3be860](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/a3be8601cce0781b05736b4fadf6435cb1130699))
* implement proposals for project ideas ([a5b7cb4](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/a5b7cb4f10a9b71f81bd3d4e5129d36d53348d70))
* make cron configurable ([86f4bce](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/86f4bceaba8052bb3360534a8328d1aaa3980a94))

### [2.1.1](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.1.0...v2.1.1) (2022-06-16)


### Bug Fixes

* finalize migration ([baee8d6](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/baee8d6784193ad898bd6f2fea8b3092875f3c5b))

## [2.1.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.0.1...v2.1.0) (2022-06-16)


### Features

* make supervisors explicit references ([5a42c27](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/5a42c27d826ae66ae9f5ccef35e7db42ae7ab53a))

### [2.0.1](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v2.0.0...v2.0.1) (2022-05-26)


### Bug Fixes

* drop context column ([2ceb3a7](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/2ceb3a7a7e77fe2806483b2c9befc0a01d673ffe))

## [2.0.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.4.6...v2.0.0) (2022-05-26)


### ⚠ BREAKING CHANGES

* simplify repository and exported resources
* implement ownership concept

### Features

* add endpoint for authenticated user ([8d57592](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/8d57592ff869c8b8e70fbdc3f1e702a2a9f07ec4))
* add organization check ([a776c72](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/a776c728908e9d4734d1f4513fa96f62c26cced4))
* add permissions to response body ([c9f22a9](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/c9f22a9368636f413a956127086a431c9caa6e36))
* add sort ([76614d8](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/76614d8e6630e64336743b9ec4ff546eca61a9ab))
* implement ownership concept ([af0c454](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/af0c454dcfc1f29f59ddc5999663912ae0893ffa))
* introduce endpoints to retrieve owners projects ([98aa34d](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/98aa34dbd7cececd28a49d34eee9dc87ef25d70e))


### Bug Fixes

* make owner ids accessible ([d4050c2](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/d4050c2904e423ae07a35f2795990300da953536))


* simplify repository and exported resources ([d28672b](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/d28672bbd454c94369caab54fe039a73cf073bde))

### [1.4.6](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.4.5...v1.4.6) (2022-04-04)

### [1.4.5](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.4.4...v1.4.5) (2022-03-30)


### Bug Fixes

* temporary fix for SpringShell ([cd49532](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/cd49532227e73bd600c36717c08a5c9083c32716))

### [1.4.4](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.4.3...v1.4.4) (2022-02-27)


### Bug Fixes

* add specializations to search ([e85562d](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/e85562d08b7788475414f759ebd4da4008c62d66))

### [1.4.3](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.4.2...v1.4.3) (2022-02-27)


### Bug Fixes

* array parameter to search moduletypes based on specialization ([de42f30](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/de42f3088ab34d8aef93187d3fea6754f83b86a2))

### [1.4.2](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.4.1...v1.4.2) (2022-02-27)


### Bug Fixes

* permit specializations route ([de444ba](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/de444ba16652a71a8df95c3199a6efbeb5e82fd0))

### [1.4.1](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.4.0...v1.4.1) (2022-02-27)


### Bug Fixes

* add specializations to projection ([ff81f8d](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/ff81f8d150521af668e27fa5007c3f2f3d5bb147))

## [1.4.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.3.1...v1.4.0) (2022-02-27)


### Features

* add query methods for specializations based on modules ([ddaaa02](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/ddaaa02a3a7131f7c4cc11938fdd9b0877f47734))
* add specializations to projects ([b19037a](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/b19037ac2e696e5f8bee0494510c63c60bfd69e5))


### Bug Fixes

* add swagger-ui to public paths ([b7a89a0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/b7a89a0bada94eec41f083f08a8a8452a054d935))
* configure read-only REST resources for specialization, moduletypes and studyprograms ([fc4bd5b](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/fc4bd5b3150c19cec38963a3aeed366ea0168edf))

### [1.3.1](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.3.0...v1.3.1) (2022-02-15)

## [1.3.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.2.5...v1.3.0) (2022-01-31)


### Features

* sorting capabilities for all rest resources ([de16596](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/de165964d5fa75dfd93377b69514d11fba977aed))

### [1.2.5](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.2.4...v1.2.5) (2022-01-31)


### Bug Fixes

* fix projection ([4c126dc](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/4c126dcbc3d8f77d1c2494ee5279c61e63719683))

### [1.2.4](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.2.3...v1.2.4) (2022-01-30)


### Bug Fixes

* creator auditing and permission evaluating ([46d1fe8](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/46d1fe85a1da0fa207ec2ef3655ce483c560ed45))

### [1.2.3](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.2.2...v1.2.3) (2022-01-16)


### Bug Fixes

* remove tostring and apply-safe-navigation ([e7272d0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/e7272d0a3f899682ffa4e820c77f85d83e1e0a1c))

### [1.2.2](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.2.1...v1.2.2) (2022-01-15)


### Bug Fixes

* remove validations for auto-generated properties ([4735ef0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/4735ef0729e9ed4ff5ab34df78533e4277a36ab4))

### [1.2.1](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.2.0...v1.2.1) (2022-01-14)


### Bug Fixes

* include ID in projection ([d32cec1](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/d32cec1c09570a98c0bab625480ecfb7be01f7cf))

## [1.2.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.1.0...v1.2.0) (2022-01-14)


### Features

* add a project projection that includes modules ([afadb0e](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/afadb0e13f0af78371b06d543af79cb25c859533))


### Bug Fixes

* add missing projection annotation ([8e722f6](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/8e722f62ddec84004240ea63b531c0be600ff485))

## [1.1.0](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.0.1...v1.1.0) (2022-01-14)


### Features

* add a projection which includes modules in study programs ([e169b56](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/e169b56b9a34bee76192a122f26ae3073127a42e))

### [1.0.1](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/compare/v1.0.0...v1.0.1) (2022-01-14)

## 1.0.0 (2021-12-11)

### Bug Fixes

- adjust keycloak version and settings ([9fc6f03](https://github.com/innovation-hub-bergisches-rheinland/prox-project-service/commit/9fc6f035947f420a8afdcc12997d20e787b4b072))
