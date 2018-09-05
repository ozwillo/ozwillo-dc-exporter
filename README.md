# DC Exporter

DC Exporter is a data synchronizer between the [Ozwillo Datacore](https://github.com/ozwillo/ozwillo-datacore) and the Open Data platform [CKAN](https://github.com/ckan/ckan)

### Features 

- Configure model mapping synchronization between Datacore and CKAN
- Choose fields to publish in Open Data
- Configure datasets metadata
- On-the-fly geocoding of resources
- List model mapping synchronizations 
- Links to CKAN datasets and resources 

### Prerequisites

Building requires : 
- **Java 8**
- **MongoDB 2.6**
- **Node 5.6.0**
- **Ozwillo ecosystem**(Kernel,Datacore,etc)

### Installing
* Clone this repository
```
git clone https://github.com/ozwillo/ozwillo-dc-exporter/
```
* After cloning this repository, install the npm packages

```
nvm install 5.6.0 (or nvm use 5.6.0 if you already have Node 5.6.0 installed)
npm install
```

### Running the DC Exporter

* Run Sring Boot 
```
./gradlew bootRun
```

* Run webpack-dev-server
```
npm run start
```
Open [http://localhost:3000](http://localhost:3000)

NB: to be used, DC Exporter features require Kernel and Datacore servers to be deployed and configured in [application.yml](https://github.com/ozwillo/ozwillo-dc-exporter/blob/master/src/main/resources/application.yml).

