cat handleMails |grep -Ei --color "MAIL\[.*\] URL"  -o|sort -u|grep -Ei "\[.*\]" -o|sort -u
