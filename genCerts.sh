#!/bin/bash

if (($# < 1))
then
    echo -e "Missing argument : Common name."
    exit
fi

rm spem2.key spem2.csr spem2.crt spem2.pem

openssl genrsa -out spem2.key 2048

expect expectGenCsr $1

openssl x509 -req -days 36500 -in spem2.csr -signkey spem2.key -out spem2.crt

cat spem2.key > spem2.pem

cat spem2.crt >> spem2.pem

mv spem2.pem covoituliege_user_tracking/spem2.pem

mv spem2.crt server/spem2.crt

mv spem2.key server/spem2.key

rm spem2.csr