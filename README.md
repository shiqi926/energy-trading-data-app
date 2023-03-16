# Readme file for Olive Project
This is an energy trading data application that scrapes trading sites for the prices of different oils and gases from various countries (New Zealand, Japan) and visualises it on a dashboard.

## Compile and run Olive App for development

Type `./mvnw spring-boot:run` into command line

## Compile and deploy Olive App to heroku

Type `./mvnw clean heroku:deploy` into command line

## Deployed link
`https://oliveproject.herokuapp.com/`

## Frontend setup

```
yarn install
```

### Compiles and hot-reloads for development

```
yarn serve
```

### Compiles and minifies for production

```
yarn build
```

### Lints and fixes files

```
yarn lint
```

### Customize configuration

See [Configuration Reference](https://cli.vuejs.org/config/).

### Valid date range

New Zealand: January 2002 - June 2021
Japan: April 2021 - September 2021

You're done :)))