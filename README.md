# JDriven Blogtober 2020: Spring Web Frameworks vergeleken

Sinds 2005 heb ik gewerkt met Spring Framework, vanaf versie 1.25. In de afgelopen 15 jaar is er nogal wat veranderd.
Daarnaast zijn er vele concullegas bijgekomen (Micronaut, Quarkus, Javalin, etc.), maar de ongevenaarde kwaliteit en
reikwijdte van het hele Spring landschap is nog steeds een reden voor het gebruiken van Spring.

In deze repo vindt de belangstellende drie modules, die allen dezelfde functionaliteit implementeren, maar met
verschillende methodes om die functionaliteit als een REST API aan te bieden:

1. **Spring Web**: traditioneel, nog net niet met XML voor de configuratie, maar wel veel annotaties
2. **Spring WebFlux**: meer functioneel, zeker omdat ik gekozen heb om functionele endpoints variant te implementeren.
3. **Spring JaFu**: geen annotaties, nog in de kinderschoenen, maar mogelijk een (de?) toekomstige methode om met Spring REST APIs
te bouwen?

## Badge

![GitHub Action](https://github.com/jvorhauer/spring-webs-blog/workflows/Clean%20Test/badge.svg)

## Work In Progress

Dit project is nog niet klaar. Zodra dit project in een stadium komt dat er op compleetheid gereageerd kan worden, verdwijnt deze melding.

## Test

```mvn clean test``` in het parent project zorgt ervoor dat alle tests in de drie modules doorlopen worden.

## Annotaties

Naarmate er meer moderne Spring REST frameworks gebruikt worden (oplopend van 1 tot en met 3), wordt het aantal gebruikte
annotaties steeds minder.
Daar wordt ik persoonlijk best blij van. Annotaties zijn handig en nuttig, maar zorgen ook voor een stukje magie, die niet altijd
goed beredeneerd kan worden (definitie van magie eigenlijk) en niet of lastig te debuggen is.
Als het mogelijk is om annotaties te vermijden, dan is dat mijn aanbeveling. En gelukkig maakt Spring, met name het Spring Fu project, het steeds beter mogelijk om geheel zonder annotaties te werken.

NB: Lombok is nog steeds vaak handig, maar het ziet er naar uit dat de meest gebruikte annotaties van Lombok minder nopdig worden: Java records (tweede preview in JDK 15) bieden mogelijkheden zoals case classes in Scala en data classes in Koltin. Daarmee verdwijnt het nut van @Data, @Getter, @Setter, @Value, @AllArgsConsttructor, etc. voor zogenaamde Java beans steeds meer.

## Lessons Learned

Vanzelfsprekend heb ik een hoop geleerd tijdens het bouwen van deze drie modules.

Belangrijkste is ongetwijfeld de ervaring met project Reactor en WebFlux: jaren geleden was ik best veel bezig met reactive extensions voor Java (RxJava), maar dat verdween van de radar toen ik Scala ging doen. Terug bij Java bleek Vavr veel van Scala standaard library naar Java te hebben gebracht. ```Mono``` en ```Flux``` maken Vavr echter weer minder handig in combinatie met Vavr, maar na enige gewenning is project Reactor goed in staat om reactief programmeren te ondersteunen.

Ook testen gaat anders met WebFlux en JaFu. In plaats van de Spring web test annotaties moet er meer zelf gedaan worden. Wel meer controle daardoor en toch niet zoveel extra code als op het eerste gezicht nodig leek.

JSON serialiseren en deserialiseren blijft een gevecht met Java. Veel tijd gekost om Jackson uit te leggen wat ik wil. Behalve bij de JaFu module! Daar is geen enkele extra configuratie noodzakelijk. Scheelt weer in obscure settings uitleggen aan degene die de code wil verbeteren en/of uitbreiden.

GitHub Actions aanmaken en uitvoeren was ik al een tijdje van plan en dit project leek daar ideaal voor: geen moeilijke deploys, alleen bouwen en testen. De documentatie van GitHub Actions is oké, maar soms is het wel even zoeken. Aan de andere kant: werkt nu naar volle tevredenheid.
