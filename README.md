# SP1 - Movie Repository

## Projektbeskrivelse
Dette projekt er et **ETL-system** (Extract, Transform, Load), der henter data fra **The Movie Database (TMDb) API**, konverterer det til interne DTO’er/entities og gemmer det i en **PostgreSQL database**.  
Projektet er den først SP-projekt dette semster.

Formålet er at:
- Hente filmdata (inkl. directors, genres og actors) via eksternt API.
- Konvertere og mappe data til interne Java-objekter.
- Persistere data i en database med JPA/Hibernate.
- Køre automatiserede tests med Testcontainers (Docker).

### Gruppe medlemmer
- Asger
- Daniel
- Mariam

---

## Setup

### Teknologier 
- **Java 17**
- **Maven** (build & dependency management)
- **Docker** (bruges til Testcontainers)


### Struktur (packages vi har brugt)
- Projektet er opdelt i følgende packages:
- **app.config** – konfiguration (EntityManagerFactory, database setup).
- **app.daos** – Data Access Objects, ansvarlige for at hente/gemme entities i DB.
- **app.dtos** – Data Transfer Objects, bruges til at holde data fra API’et.
- **app.entities** – Entities (JPA-klasser), som repræsenterer tabeller i databasen.
- **app.exceptions** – Egen-definerede exceptions.
- **app.mappers** – Mapper DTO’er til Entities og omvendt.
- **app.services** – Servicelag til at kalde eksterne API’er og håndtere logik.
- **app.utils** – Hjælpeklasser (fx konstanter, værktøjsmetoder).
- **app.Main** – Startpunktet for programmet.

### Kørsel af tests
For at køre tests (kræves Docker, da der bruges Testcontainers):
```java
mvn test
```

### API nøgle
Projektet kræver en API-nøgle til TMDb.
  ```bash
  export API_KEY=din_api_nøgl
```

