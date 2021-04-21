# develop stage
FROM node:11.10.1-alpine as develop-stage
WORKDIR /app
COPY package*.json ./
RUN yarn global add @quasar/cli
COPY . .
# build stage
FROM develop-stage as build-stage
RUN yarn
RUN yarn upgrade
RUN quasar build -m pwa
# production stage
FROM nginx:1.15.7-alpine as production-stage
COPY ./build/docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build-stage /app/dist/pwa /usr/share/nginx/html
COPY ./build/docker/ece-start /usr/bin/ece-start

RUN apk add --update \
    curl \
    bash \
    && rm -rf /var/cache/apk/* \
    && chmod a+x /usr/bin/ece-start

EXPOSE 80
CMD ["/usr/bin/ece-start"]
