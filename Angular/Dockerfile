FROM node:14-alpine as node
WORKDIR /app
COPY . .
#RUN export NODE_OPTIONS=--openssl-legacy-provider
RUN npm install --force
RUN npm run build --prod

FROM nginx:alpine
COPY --from=node /app/dist/ /usr/share/nginx/html
