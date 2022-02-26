# If non M1 chip use:
# FROM ubuntu

FROM arm64v8/ubuntu

RUN apt-get update \
  && DEBIAN_FRONTEND=noninteractive  apt-get install -y collectl \
  && rm -rf /var/lib/apt/lists/*

COPY ./collectl/entrypoint.sh /
RUN chmod +x /entrypoint.sh

ENTRYPOINT [ "/entrypoint.sh" ]
