FROM postgres
RUN export TERM=xterm
COPY init.sh /docker-entrypoint-initdb.d/
RUN chmod 0755 /docker-entrypoint-initdb.d/init.sh
RUN echo "host all  all    0.0.0.0/0  md5" >> /var/lib/postgresql/pg_hba.conf
RUN echo "listen_addresses='*'" >> /var/lib/postgresql/postgresql.conf
RUN echo "export TERM=xterm" >> /root/.bashrc
RUN apt-get update && apt-get -y install vim
EXPOSE 5432
