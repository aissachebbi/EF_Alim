# CB MSG Feeder (Spring Boot + JDBC, Java 17)

## Objectif

Application Spring Boot qui exécute un poller configurable et alimente:
- `ACETP.CB_MSG`
- `ACETP.CL_BUSINESS_MTM_IN`

À chaque cycle:
1. Tirer un volume aléatoire `1..N` (`N` configurable).
2. Insérer ce volume dans `CB_MSG` avec les champs obligatoires.
3. Insérer le même volume dans `CL_BUSINESS_MTM_IN` avec la référence `CB_MSG_DB_ID`.
4. Renseigner explicitement les dates en DateTime (`CREATION_DATE`, `UPDATING_DATE` dans `CB_MSG`, et `CREATION_DATE` dans `CL_BUSINESS_MTM_IN`).

## Stack technique

- Java 17
- Spring Boot
- Spring JDBC
- Oracle JDBC (`ojdbc11`)
- Pas de Spring Test

## Configuration Oracle (application.yml)

La base est configurable via `src/main/resources/application.yml` (ou variables d’environnement):

```yaml
server:
  port: 8082

spring:
  datasource:
    url: ${DB_URL:jdbc:oracle:thin:@//localhost:1521/FREEPDB1}
    username: ${DB_USERNAME:ACETP}
    password: ${DB_PASSWORD:Aissa1000*}
    driver-class-name: ${DB_DRIVER:oracle.jdbc.OracleDriver}

app:
  feeder:
    poll-interval-ms: ${FEEDER_POLL_INTERVAL_MS:10000}
    max-messages-per-run: ${FEEDER_MAX_MESSAGES_PER_RUN:1000}
    cb-msg-sequence-name: ${FEEDER_CB_MSG_SEQ:ACETP.BDOMO_GRM_TRD_CB_MSGS_DB_ID_Test}
    cl-business-file-sequence-name: ${FEEDER_CL_FILE_SEQ:ACETP.SEQ_CL_BUSINESS_FILE_ID}
```

## Scripts SQL manuels (sans composant Java)

Les tables existent déjà en base dans votre contexte. Les scripts restent fournis pour alignement/rejeu manuel si nécessaire.

Les scripts SQL sont fournis séparément pour exécution manuelle:

- `src/main/resources/sql/ddl-feeder.sql`
  - crée les séquences Oracle utilisées par l'application (`ACETP.BDOMO_GRM_TRD_CB_MSGS_DB_ID_Test`, `ACETP.SEQ_CL_BUSINESS_FILE_ID`)
  - crée `ACETP.CB_MSG`
  - crée `ACETP.CL_BUSINESS_MTM_IN`
  - crée les index alignés au DDL fourni
- `src/main/resources/sql/truncate-feeder.sql`
  - purge les 2 tables via `TRUNCATE TABLE`
- `src/main/resources/sql/drop-feeder.sql`
  - supprime les 2 tables (`DROP ... CASCADE CONSTRAINTS PURGE`)
  - supprime aussi les 2 séquences feeder (`DROP SEQUENCE`)

Exemple d'ordre manuel:
1. `drop-feeder.sql`
2. `ddl-feeder.sql`
3. (optionnel) `truncate-feeder.sql` pour vider les tables

## Lancement

```bash
mvn spring-boot:run
```

## Séquences Oracle et application.yml

Les noms de séquences sont configurés dans `application.yml`:
- `app.feeder.cb-msg-sequence-name`
- `app.feeder.cl-business-file-sequence-name`

L'application **ne crée pas automatiquement** ces séquences. Elle fait uniquement un `SELECT <sequence>.NEXTVAL FROM DUAL` au moment des insertions.

Donc, avant de lancer l'application, il faut exécuter le DDL (`ddl-feeder.sql`) pour créer les séquences par défaut.

Si vous changez les noms dans `application.yml`, vous devez aussi créer ces nouvelles séquences côté Oracle (ou adapter le script SQL).
