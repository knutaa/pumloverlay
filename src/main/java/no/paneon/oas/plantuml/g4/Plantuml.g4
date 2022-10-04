grammar Plantuml;

uml:
    START
    statements*
	END
    // (NEWLINE)* EOF
    ; 
    
statements
    : class_declaration 
    | connection 
    | enum_declaration 
    | hide_declaration
   	| show_statement 
   	| skinparam_declaration 
   	| legend_statement
   	| COMMENT
   	| NEWLINE
    ;
    
hide_declaration: 
    'hide' type=('methods' | 'stereotype' | 'circle')
    ;


show_statement:
	'show' stereotype 'stereotype'
	;

legend_statement:
	'legend'
	('|' line | line | NEWLINE)*
	'endlegend'
	;
	
class_declaration:
    class_type ident stereotype* ( LPAR NEWLINE?
    // (prefix attribute | prefix method | prefix assignment | NEWLINE)*
    // (line cardinality? | NEWLINE)*
    (blank | attribute | assignment | NEWLINE)*
    (delimiter NEWLINE)?
	discriminator?
    RPAR )?
    ;
    
delimiter: '--' ;

cardinality: 
	// '[' ~(']')* ']'
	'[0..*]'
	;
	
discriminator:
    DISCRIMINATOR ( color? line | NEWLINE)*
	;
	
assignment:
	ident '=' value
	;
	
skinparam_declaration:
    'skinparam' type=('class' |'legend') LPAR NEWLINE?
    // (prefix? ident stereotype? value postfix? | NEWLINE)*
    (line | NEWLINE)*
    RPAR
    |
    'skinparam' ident value
    ;
	
attribute:
	color?
	attr_field?
    visibility?
    modifiers?
    type_declaration?
    ':'?
    ident
    mandatory?
    cardinality?
    ;

mandatory: '(1)' ;
 
attr_field:
	'{field}'
	;
	
blank: '{field}//' '//' ;

method:
    visibility?
    modifiers?
    type_declaration?
    ident
    '(' function_argument_list? ')'
    NEWLINE
    ;

connection_left:
    instance=ident ('"' attrib=ident mult=multiplicity? '"')?
    ;

connection_right:
    ('"' attrib=ident mult=multiplicity? '"')? instance=ident
    ;

//connection:
//    ident line
//    ;
    
//connection:
//    left=connection_left
//    leftMultiplicity=multiplicity?
//    CONNECTOR 
//    rightMultiplicity=multiplicity?
//    right=connection_right
//    (':' property=ident)? // WAS (':' stereotype)?
//    NEWLINE
//    ;

connection:
    left=connection_left
    leftMultiplicity=multiplicity?
    CONNECTOR 
    rightMultiplicity=multiplicity?
    right=connection_right
    (':' property_color=color? property=ident)? // WAS (':' stereotype)?
    NEWLINE
    ;

//multiplicity: ('*'  | '0..1'   | '0..*'   | '1..*'   | '1'   | 
//              '"*"' | '"0..1"' | '"0..*"' | '"1..*"' | '"1"' );


multiplicity: QSTR ;

visibility:
    '+'     
    |'-'    # visibility_private
    |'#'    # visibility_protected
    ;

field:
    color? LPAR ident RPAR    
    ;
   
     
function_argument:
    type_declaration? ident
    ;

function_argument_list:
    function_argument (',' function_argument)*
    ;


template_argument:
    type_declaration
    ;

template_argument_list:
    template_argument (',' template_argument)*
    ;

ident:
    IDENT
    ;

modifiers:
    static_mod='{static}'
    | abstract_mod='{abstract}'
    ;

stereotype:
    '<<' name=ident('(' args+=ident ')')? '>>'
    ;

type_declaration:
    ident '<' template_argument_list? '>'               # template_type
    | ident '[' ']'                                     # list_type
    | ident                                             # simple_type
    ;

class_type:
    'abstract' 'class'?
    | 'class'
    | 'interface' 'class'?
    ;

item_list:
    (ident NEWLINE)+
    ;

enum_declaration:
    'enum' ident stereotype* (LPAR NEWLINE
   	// item_list?
   	(line | NEWLINE)*
    RPAR )?
    ;

CONNECTOR:
    '--'
    | '.' DIR? COLOR? '.'
    | '-' DIR? COLOR? '->'
    | '<-' DIR? COLOR? '-'
    | '-' DIR? COLOR? '-*'
    | '*-' DIR? COLOR? '-'
    | '-' DIR? COLOR? '-o'
    | 'o-' DIR? COLOR? '-'
    | '<|-' DIR? COLOR? '-'
    
    | '<|--'
    | '--|>'
       
    | '-' DIR? COLOR? '-|>'
    | '.' DIR? COLOR? '.|>'
    | '<|.' DIR? COLOR? '.'
    | '*-' DIR? COLOR? '->'
    | '<-' DIR? COLOR? '-*'
    | 'o-' DIR? COLOR? '->'
    | '<-' DIR? COLOR? '-o'
    | '-'
    | '.'
    | '->'
    | '<-'
    | '-*'
    | '*-'
    | '-o'
    | 'o-'
    | '<' DIR? HIDE? COLOR? '|-'
    | '-' DIR? HIDE? COLOR? '|>'
    | '.' DIR? HIDE? COLOR? '|>'
    | '<|' DIR? HIDE? COLOR? '.'
    | '*->'
    | '<-*'
    | 'o->'
    | '<-o'
   
    | '-' DIR? HIDE? COLOR? '->'
    | '-' DIR? HIDE? COLOR? '->'
    | '-' DIR? HIDE? COLOR? '->'
    | '-' DIR? HIDE COLOR? '-|>'
     
//    | '-right' HIDE? COLOR? '->'
//    | '-left' HIDE? COLOR? '->'
//    | '-left' HIDE? COLOR? '->'
//    | '-right' HIDE COLOR? '-|>'
    
    | '*-right' HIDE? COLOR? '->'
    | '*-left' HIDE? COLOR? '->'
    | '*-left' HIDE? COLOR? '->'
    | '-right' HIDE COLOR? '-|>'
    | '.' DIR? COLOR? '.>' 
    
    | '<.' DIR? COLOR? '.' 
    
    ;

	


color:
	'<' 'color:' IDENT '>'
	;
	

line:
     ~( DISCRIMINATOR | ENDLEGEND | RPAR | LBRAC | NEWLINE )+ 
     ;
        

DIR : ('left' | 'right') ;
     
HIDE : '[hidden]' ;

LPAR : '{' ;
RPAR : '}' ;
LBRAC: '[' ;

COLOR:
	'[#' IDENT ']'
	;
	   
DISCRIMINATOR: 'discriminator:';

ENDLEGEND: 'endlegend' ;


START: '@startuml' ;
END: '@enduml' ;

NEWPAGE : 'newpage' -> channel(HIDDEN)
    ;

NEWLINE  :  ENDLINE ; // [\r\n]+; // '\r'? '\n'; // [\r\n];

IDENT : NONDIGIT ( DIGIT | NONDIGIT )*;
NUMBER : ( DIGIT )+;
HEX : '#' ( DIGIT | NONDIGIT )+;

QSTR : '"' ~'"'+ '"' ;

value : (IDENT | NUMBER | HEX);

COMMENT :
    ('\'' .*? NEWLINE)  -> channel(HIDDEN)
    ;
    
WS  : [ ]+ -> channel(HIDDEN); // toss out whitespace
   
//=========================================================
// Fragments
//=========================================================
fragment NONDIGIT : [_a-zA-Z#@-];
fragment DIGIT :  [0-9];
fragment UNSIGNED_INTEGER : DIGIT+;
fragment ENDLINE  :  [\r\n]+; 

