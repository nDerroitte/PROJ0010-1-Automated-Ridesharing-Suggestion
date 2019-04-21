import 'package:flutter/material.dart';

/// Tracking algorithm parameters
const minDistanceNewJourney = 1000.0; // 1000 meters
final minPauseTimeBetweenJourneys = Duration(minutes: 20);
const dateFormat = 'yyyy-MM-dd HH-mm-ss';

/// Location Listener parameters
const timeIntervalBetweenPoints = 1000; // 2 min 30
const maxWaitTimeForUpdates = 5000; // 1 hour
const minDistanceBetweenPoints = 100.0; // 100 meters

/// Different text styles.
const _fontSize = 18.0;
const textStyle = TextStyle(fontSize: _fontSize, color: Colors.black);
const warningStyle = TextStyle(fontSize: _fontSize, color: Colors.red);
const linkStyle = TextStyle(fontSize: _fontSize, color: Colors.blue);
const smallInfoStyle = TextStyle(fontSize: _fontSize, color: Colors.black);
const smallWarningStyle = TextStyle(fontSize: _fontSize, color: Colors.red);
const confirmationStyle =
    TextStyle(fontSize: _fontSize, color: Colors.lightGreenAccent);

const serverURL = "https://spem2.montefiore.ulg.ac.be/";
const serverError =
    'Erreur serveur, veuillez réessayer. Si le problème persiste, merci de contacter le service technique.';

const httpError = -1;

/// Possible connection results.
const invalidUsername = 0;
const invalidPassword = 1;
const passwordOK = 2;
const anonymousConnexion = 7;

/// Possible sign up results.
const signUpOK = 3;
const invalidEmail = 4;

/// Possible forgottenPassword results.
const forgottenPasswordOK = 5;

/// Possible storeData results.
const storeDataOK = 6;

/// Graphical constants.
final buttonColor = Colors.lightBlue[800];
final backgroundColor = Colors.lightBlue[50];
final appBar = AppBar(
  title: Center(child: Text('Ugo           ')),
  // Whitespaces are used for centering (do not delete)
  flexibleSpace: Container(
    decoration: new BoxDecoration(
      gradient: new LinearGradient(
        colors: [
          const Color(0xFF3366FF),
          const Color(0xFF00CCFF),
        ],
        begin: Alignment.topRight,
        end: Alignment.topLeft,
        stops: [0.0, 1.0],
        tileMode: TileMode.clamp,
      ),
    ),
  ),
);

/// Text relative to the users' personal data usage.
final gdprText = Padding(
  padding: const EdgeInsets.symmetric(horizontal: 10.0),
  child: ListView(
    children: <Widget>[
      Text(
        "Dans le cadre de l'automatisation de la récolte des données nécessaires au service CovoitULiège, " +
            "à savoir les trajets futurs de ses utilisateurs, cette application récolte et transfère " +
            "la position de ses utilisateurs, régulièrement, en vue d'analyse.\n\n" +
            "Les données recueillies dans ce formulaire (la localisation de l'appareil, " +
            "étant conservées en particulier la latitude et la longitude, ainsi que la seconde associée) " +
            "sont enregistrées dans un fichier informatisé par l'équipe de développement de CovoitULiège " +
            "de l'Université de Liège afin de " +
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
            "en contactant Covoituliege@uliege.be ou, à défaut, le Délégué à la Protection des Données de l’ULiège " +
            "(dpo@uliege.be – Monsieur le Délégué à la Protection des Données, Bât. B9 Cellule « GDPR », Quartier Village 3, " +
            "Boulevard de Colonster 2, 4000 Liège, Belgique). Vous disposez également du droit d’introduire une réclamation " +
            "auprès de l’Autorité de protection des données (https://www.autoriteprotectiondonnees.be, contact@apd-gba.be).",
        style: TextStyle(fontSize: 22.0, fontStyle: FontStyle.italic),
        textAlign: TextAlign.justify,
      ),
    ],
  ),
);
