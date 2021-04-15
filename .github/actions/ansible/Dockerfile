FROM ubuntu

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y ansible gpg

COPY appery_key2.gpg /appery_key2.gpg
COPY e_playbook.yml /e_playbook.yml
COPY e_inventory.yml /e_inventory.yml
COPY entrypoint.sh /entrypoint.sh
COPY bball.jar bball/bball-app.jar

ENTRYPOINT ["bash", "/entrypoint.sh"]