# Tävlingsplaneraren

## Att göra kortsiktigt
1. Fundera på hur man returnerar spelarnummer i poolen och matchordning

## Att göra långsiktigt
1. Sätt upp defaulttävling man kan hämta ut när man skapar nytt
2. lägg in data genom sql istället?

## Scope och !Scope
Sätta upp (1) och hålla (2) tävlingar inom bordtennis
1. Ange tävlingsinfo, start och slutdatum
2. Göra tävlingen tillgänglig online så att andra klubbar kan anmäla sina spelare (inga email, och mycket mindre ansvar för tävlingsledare)
3. Förifyllda klasser där klubbarna lägger in sina spelare. Tävlingsform (pool eller cup) anges
4. Lottning enligt förvalda kriterier. Rankingpoäng hämtas från central databas, seedning görs av oss baserat på anmälningar.
5. Tidssättning/schema. Man bör kunna ange i vilken ordning klasser ska spelas, få ett turnummer för varje match och sen automatiskt skapa scheman för klasserna baserat på antal bord, deltagare och klasser.
6. Resultatrappoortering. Man bör kunna mata in resultatet per match och vinnaren räknas ut baserat på tävlingssystemet valt för den klassen. Att kunna rapportera in löpande i en app vore en fin bonus via "plus"- och "minus"-knappar
7. Fakturor: Få kostnad totalt för en klubb och för en spelare, möjlighet att skapa och skicka fakturor. 

### !Scope
-  Gratistjänst där man kan anordna sina egna tävlingar
- Egen funktionalitet för ranking, poäng -- detta bör hämtas


##

## Konkurrenter
Lite konkurrentresearch som bakgrund och för idé generering.


### Tournify

Lite oproffisionellt (buggar, sträng-mismatch, inkonsekvent layout) men klart bäst turneringsplaneringstjänster som kommer upp vid en googling. Gratis, online och har appar.

##### Flöde för att anordna tävling:
1. Ange namn på tävling och startdatum (tryck på knapp för fler datum)
2. Plats där tävlingen hålls (autofylls när man börjar skriva!)
3. Skriv in divisioner
4. Ange namn login och börja administrera
5. Tab för administratörer

##### När tävling är uppsatt finns:
- Deltagare (mata in deltagare), lägg till i en division. Spelare kan vara i en division men verkar inte kunna tillhöra en klubb. Här kan man också lägga till extra administratörer.
- Format (grupp, grupp och utslagning, enbart utslagning). Eller bygg ihop själv
- Schema: Man lägger in matcher manuellt. Skriv in en bana och välj spelare från matcher som finns (givet inlagda spelare och tävlingsform)
- Presentation: Visa resultat som hemsida på storbildsskärm. Väldigt snyggt, man skapar sin egen url. Med kostumiseringsbar bakgrund och plats för logga. Det borde inte behövas -- de flesta kunder komma att vara en del av ett större inköp.
- Resultat: Resultaten matas inte in vid schemat/planering utan via en separat tab (results). Här ser du matcherna, kan mata in resultat, och starta faser.

##### Review
Enkel, smidig, och bra presentationsfeature. Oklart flöde -- det är inte uppenbart för en ny användare hur man ska gå tillväga för att binda ihop spelare, tävlingsform, schema och resultat.
Ingen möjlighet att hämta data från andra system eller koppla spelarna mot en databas. Därmed ingen direkt konkurrent.

## tournamentscheduler.net

Extremt enkelt -- 7 textfält på samma sida, sen skapas tävlingen.
Inte jätteavancerad men tredje unika resultatet på google (efter tournament planner och tournify)
25000 scheman har skapats.

## Instantliga
Snyggare, nyare, mer professionell tjänst än de andra. Kan inte jobba via hemsidan, måste installera telefonapp.
Många olika defaultsporter att välja på. Däremot oklara instruktioner/inte lätt att komma igång. Speciellt efter man behöver appen först.
Min gissning är att de flesta inte vill sätta med en telefon när de lägger in spelare, skapar scheman, etc -- det är ganska mycket jobb som kräver utrymme.

Bra feature -- man kan följa en skapa tävling. Man får ett id-nummer att knappa in som gör att man följa/bli notifierad om uppdateringar. Ett alternativ till den autogenererade hemsidan tournify har.

Oacceptabelt dålig för sporter med set -- man kan inte rapportera setresultat eller ställa in antal set i en match. Iställer är tipset:
"The simplest way to handle the score for sports that use sets, is to use the integers for the sets, and the comments for set details. "
Så att sätta 3-1 i setresultat och sedan skriva in exakt resultat känns helt för vagt. Helst vill man kunna rapportera in setresultat löpande. Så antagligen är appen designad för fotboll och andra bollsporter utan set.  