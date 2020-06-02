FROM node:slim AS build
WORKDIR /app
RUN npm set unsafe-perm true

COPY ./gateway.js ./package.json ./
ENTRYPOINT ["npm", "start"]
