//got bored on 4/8/22 and decided to program wordle. finished on 4/9
//if you input a word longer or shorter than five words, it ends the program. no second chances. 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <ctype.h>
#include <stdbool.h>

char* stringInput(){
 /* gets input string */
    char *string = malloc(strlen(string));
    printf("Enter guess: ");
    fgets(string, 100, stdin); 

    /* error handling */
    if(strlen(string)-1 != 5) {
        printf("String not correct size! Run program again! Don't mess up anymore, got it?\n");
        return 0;
    }

    /* lowercasing input */
    int x = 0;
    while(string[x]) {
      string[x] = tolower(string[x]);
      x++;
    }
    //printf("Length of string = %d\n", strlen(string));

    return(string);
}

char* gameLogic(const char* string, const char* ansr) {
    /* LOGIC OF GAME */
    
    int count = 0;
    int arrk[5];
    int arrj[5];
    bool krepeat = false;
    bool jrepeat = false;
    for(int k = 0; k < 5; k++){
        for(int j = 0; j < 5; j++){
            /*for loop checking if the current j is already present in its array*/
            for(int m = 0; m <= count; m++){
                if(arrj[m] == j) jrepeat = true;
            }
            if(string[k] == ansr[j] && !jrepeat/* if jrepeat is true, if statement wont execute. if the j is already in its array */){
                //printf("STRIKE! %c and %c match!\n", string[k], ansr[j]);
                arrk[count] = k;
                arrj[count] = j;
                krepeat = false;
                jrepeat = false;
                count ++;
                break;
                
            }
            jrepeat = false;
        }
    }
    char* line = malloc(5);
    
    int m = 0;
    int ansCount = 0;
    bool present = false;
    for(int i = 0; i < 5; i++){
        for(int k = 0; k < count; k++){
            
            if(i == arrk[k]){
                present = true;
                break;
            }
        }
        /*making uppercase*/
        if(present){
            line[ansCount] = toupper(string[i]);
            ansCount ++;
        } else {
            line[ansCount] = string[i];
            ansCount ++;
        }
        present = false;
    }
    printf("\n");
    return(line);
}

char printLine(const char* line, const char* ans){
    char* trueLine = malloc(13);
    int m = 0;

    /*adding spaces to trueLine*/
    for(int l = 0; l < 13; l++){
        if(l == 0 || l == 3 || l == 6 || l == 9 || l == 12){
            trueLine[l] = line[m];
            m++;
        } else {
            trueLine[l] = ' ';
        }
    }
    trueLine[13] = '\0';

    for(int k = 0; k < 13; k++){
        if((tolower(trueLine[k]) == ans[k]) && (k == 0 || k == 3 || k == 6 || k == 9 || k == 12 )){
            printf("\e[4m%c\e[m", trueLine[k]);
        }else{
            printf("%c",trueLine[k]);
        } 
    }
    printf("\n");
    int victoryCheck = 0;

    for(int l = 0; l < 13; l++){
        if(tolower(trueLine[l]) == ans[l] && (l == 0 || l == 3 || l == 6 || l == 9 || l == 12 )){
            victoryCheck++;
        }
    }
    if(victoryCheck == 5){
        printf("\n\ncongratulations! you guessed the word!\n\n");
        exit(0);
    }

}

int main() {
    printf("\n\nWelcome to Jorge Larach's shitty version of Wordle!\n");
    printf("\nRemember: Caps characters mean that the answer contains it, and underlined means it's in the right position!");
    /* declaring input string and answer strings */
    char ch_arr[][10] = {
        "abuse", "adult", "agent", "anger", "apple", "award", "basis", "beach", "birth", "block", "blood", "board", "brain", "bread", "break", "brown", "buyer", "cause", "chain", "chair", "chest", "chief", "child", "china", "claim", "class", "clock", "coach", "coast", "court", "cover", "cream", "crime", "cross","crowd", "crown","cycle","dance","death","depth","doubt","draft","drama","dream","dress","drink","drive","earth","enemy","entry","error","event","faith","fault","field","fight","final","floor","focus","force","frame","frank","front","fruit","glass","grant","grass","green","group","guide","heart","henry","horse","hotel","house","image","index","input","issue","japan","jones","judge","knife","laura","layer","level","lewis","light","limit","lunch","major","march","match","metal","model","money","month","motor","mouth","music","night","noise","north","novel","nurse","offer","order","other","owner","panel","paper","party","peace","peter","phase","phone","piece","pilot","pitch","place","plane","plant","plate","point","pound","power","press","price","pride","prize","proof","queen","radio","range","ratio","reply","right","river","round","route","rugby","scale","scene","scope","score","sense","shape","share","sheep","sheet","shift","shirt","shock","sight","simon","skill","sleep","smile","smith","smoke","sound","south","space","speed","spite","sport","squad","staff","stage","start","state","steam","steel","stock","stone","store","study","stuff","style","sugar","table","taste","terry","theme","thing","title","total","touch","tower","track","trade","train","trend","trial","trust","truth","uncle","union","unity","value","video","visit","voice","waste","watch","water","while","white","whole","woman","world","youth"};
    char ans[5]; 
    char answer[50]; //expanded ans
    
    /* intializes random number generator & generates one*/
    srand(time(NULL));
    int r = rand() % 212;
    strcpy(ans, ch_arr[r]);
    //printf("\nAnswer: %s\n", ans);
    int n = 0;

    /*adding spaces to answer to compare*/
    for(int l = 0; l < 13; l++){
        if(l == 0 || l == 3 || l == 6 || l == 9 || l == 12){
            answer[l] = ans[n];
            n++;
        } else {
            answer[l] = ' ';
        }
    }
    answer[13] = '\0';


    //GAME EXECUTION
    printf("\nturn one!\n");
    char* inOne = stringInput();
    char* outOne = gameLogic(inOne, ans);
    printLine(outOne, answer);
    
    printf("\nonto turn two!\n");
    char* inTwo = stringInput();
    char* outTwo = gameLogic(inTwo, ans);
    printLine(outOne, answer);
    printLine(outTwo, answer);

    printf("\nonto turn three!\n");
    char* inThree = stringInput();
    char* outThree = gameLogic(inThree, ans);
    printLine(outOne, answer);
    printLine(outTwo, answer);
    printLine(outThree, answer);
    
    printf("\nonto turn four!\n");
    char* inFour = stringInput();
    char* outFour = gameLogic(inFour, ans);
    printLine(outOne, answer);
    printLine(outTwo, answer);
    printLine(outThree, answer);
    printLine(outFour, answer);
    
    printf("\nonto turn five!\n");
    char* inFive = stringInput();
    char* outFive = gameLogic(inFive, ans);
    printLine(outOne, answer);
    printLine(outTwo, answer);
    printLine(outThree, answer);
    printLine(outFour, answer);
    printLine(outFive, answer);

    printf("\nonto turn six! last turn!\n");
    char* inSix = stringInput();
    char* outSix = gameLogic(inSix, ans);
    printLine(outOne, answer);
    printLine(outTwo, answer);
    printLine(outThree, answer);
    printLine(outFour, answer);
    printLine(outFive, answer);
    printLine(outSix, answer);

    printf("\n\ndang it! the word was: %s", ans);
    exit(0);
}