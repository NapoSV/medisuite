FROM node:20-alpine AS build
WORKDIR /app
RUN npm install -g pnpm@9
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .
ARG VITE_API_BASE_URL=http://localhost:8080
ARG VITE_TENANT_SLUG=clinica-san-rafael
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_TENANT_SLUG=$VITE_TENANT_SLUG
RUN pnpm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
RUN chown -R nginx:nginx /usr/share/nginx/html && chmod -R 755 /usr/share/nginx/html
USER nginx
EXPOSE 80
