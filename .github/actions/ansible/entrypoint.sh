# Decrypt the file
mkdir $HOME/secrets
mkdir ~/.ssh
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$DKPASS" \
--output $HOME/secrets/appery_key2 ~/appery_key2.gpg
eval "$(ssh-agent -s)"
chmod 600 $HOME/secrets/appery_key2
echo -e "Host rickysquid.org\n\tStrictHostKeyChecking no\n" >> ~/.ssh/config
ssh-add $HOME/secrets/appery_key2
ssh -i $HOME/secrets/appery_key2 apper@rickysquid.org pwd
ansible-playbook -i ~/e_inventory.yml ~/e_playbook.yml