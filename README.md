# DC Exporter

DC Exporter is a data synchronizer between the [Ozwillo data core](https://github.com/ozwillo/ozwillo-datacore) and the open data platform [CKAN](https://github.com/ckan/ckan)

### Features 

- Configure model mapping synchronization between data core and ckan 
- Dashboard of following and modify mapping 

### Prerequisites

Building requires : 
- **Java 8**
- **MongoDB 2.6**
- **Node 5.6.0**
- **Ozwillo ecosystem**(Kernel,Data core,etc)

### Installing
* Clone this repository with the git submodules, add recursive git clone parameter

```
git clone --recursive https://github.com/ozwillo/ozwillo-dc-exporter/
```
NB: Dc-exporter require a git submodule [Ozwillo java spring integration](https://github.com/ozwillo/ozwillo-java-spring-integration)
* After cloning this repository, install the npm packages

```
nvm install 5.6.0 (or nvm use 5.6.0 if you already have Node 5.6.0 installed)
npm install
```

### Running the Dc-exporter

* Run Sring Boot 
```
./gradlew bootRun
```

* Run webpack-dev-server
```
npm run start
```
Open [http://localhost:3000]()

NB. to be used, Dc exporter features require Kernel and Datacore servers to be deployed and configured in [application.yml](https://github.com/ozwillo/ozwillo-dc-exporter/blob/master/src/main/resources/application.yml).

