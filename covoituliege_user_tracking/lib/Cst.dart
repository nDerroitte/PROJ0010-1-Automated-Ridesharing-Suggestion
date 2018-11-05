import 'package:flutter/material.dart';

const _fontSize = 24.0;
const textStyle = TextStyle(fontSize: _fontSize, color: Colors.black);
const warningStyle = TextStyle(fontSize: _fontSize, color: Colors.red);
const linkStyle = TextStyle(fontSize: _fontSize, color: Colors.lightBlue);

final _appBarColor = Colors.green;

final appBar = AppBar(
  title: Text(
    'User Tracking CovoitULiège',
    style: textStyle,
  ),
  centerTitle: true,
  backgroundColor: _appBarColor,
);

final rgpdText = Padding(
  padding: const EdgeInsets.symmetric(horizontal: 10.0),
  child:   ListView(
    children: <Widget>[
      Text(
        "Dans le cadre de l'automatisation de la récolte des données nécessaires au service CovoitULiège, " +
        "à savoir les trajets futurs de ses utilisateurs, cette application récolte et transfère " +
        "la position de ses utilisateurs, régulièrement, en vue d'analyse.\n\n" +
        "Les données recueillies dans ce formulaire (la localisation de l'appareil, " +
        "étant conservées en particulier la latitude et la longitude, ainsi que la seconde associée) " +
        "sont enregistrées dans un fichier informatisé par TBD de l'Université de Liège afin de " + //TODO find who is the [responsable interne du traitement - Préciser ici le nom du service de l’ULiège qui est responsable de la collecte et du traitement (y compris conservation, mise à jour et effacement) des données recueillies.]
        "les analyser, d'en déduire premièrement les trajets parcourus par l'utilisateur et dans un second temps " +
        "les trajets qu'il est probable de parcourir prochainement. Ces données seront conservées " +
        "jusqu'à ce que des données plus récentes puissent les remplacer, avec un maximum de 2 mois " +
        "qui sera, par exemple, utile en cas d'arrêt d'utilisation de l'application. " +
        "Ces données seront traitées sur la base du présent consentement.\n\n" +
        "Ces données ne seront pas transmises à des tiers. Elles ne seront pas anonymisées.\n\n" +
        "Conformément aux dispositions du Règlement Général sur la Protection des Données (UE 2016/679) " +
        "et de la loi du 30 juillet 2018 relative à la protection des personnes physiques " +
        "à l'égard des traitements de données à caractère personnel, " +
        "vous pouvez exercer vos droits relatifs à ces données à caractère personnel " +
        "(droit d’accès, de rectification, d’effacement, à la limitation, d’opposition et de retrait du consentement) " +
        "en contactant TBD ou, à défaut, le Délégué à la Protection des Données de l’ULiège " + //TODO find who is the service en charge du maintien du fichier informatique contenant ces données.
        "(dpo@uliege.be – Monsieur le Délégué à la Protection des Données, Bât. B9 Cellule « GDPR », Quartier Village 3, " +
        "Boulevard de Colonster 2, 4000 Liège, Belgique). Vous disposez également du droit d’introduire une réclamation " +
        "auprès de l’Autorité de protection des données (https://www.autoriteprotectiondonnees.be, contact@apd-gba.be).",
        style: textStyle,
        textAlign: TextAlign.justify,
      ),
    ],
  ),
);