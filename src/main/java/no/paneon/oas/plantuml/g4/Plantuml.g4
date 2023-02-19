grammar Plantuml;

uml:
    START
    statements*
	END
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
	legend_rows+
	'endlegend'
	;
	
legend_rows: 
	pipe_and_cell* NEWLINE
	;
	
pipe_and_cell:
	(pipe legend_line? | legend_line)
	;

class_declaration:
    class_type ident decoration? stereotype* ( LPAR NEWLINE?
    (blank  | attribute | assignment | NEWLINE)* 
    (delimiter NEWLINE)?
	discriminators?
    RPAR )?
    ;
    
delimiter: '--' ;

pipe: '|' ;

decoration: '<extends' decorationItem+ '>'
	;
	
decorationItem: '\\n' color? ident
	;
	
cardinality: 
	color? '[' NUMBER '..' (NUMBER|'*') ']'
	;
	
discriminators:
    DISCRIMINATOR ( discriminator | NEWLINE)*
	;
	
discriminator: 
	color? line
	;
	
assignment:
	ident '=' value
	;
	
skinparam_declaration:
    'skinparam' type=('class' |'legend') LPAR NEWLINE?
    (line | NEWLINE)*
    RPAR
    |
    'skinparam' ident value
    ;
	
attribute:
	attribute_color=color?
	attr_field?
	start_sep=separator?
    visibility?
    modifiers?
    name=name_declaration?
    type_marker=':'?
    type_color=color?
    enum_marker? 
    type_ident
    mandatory?
    cardinality?
    end_sep=separator?
    ;

type_ident:
	ident ('/' ident)? | '{' '}'
	;
	
mandatory: mandatory_color=color? mand ;

mand: '(1)' ;
 
enum_marker: 'ENUM' ;
 
attr_field:
	'{field}'
	;
	
separator: '//' ;

blank: '{field}//' '//' ;

enum_field: 
	'{field}//' ident '//'
	;

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


connection:
    left=connection_left
    leftMultiplicity=multiplicity?
    CONNECTOR 
    rightMultiplicity=multiplicity?
    right=connection_right
    (':' property_color=color? property=ident)? // WAS (':' stereotype)?
    NEWLINE
    ;

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

//type_declaration:
//    ident '<' template_argument_list? '>'               # template_type
//    | ident '[' ']'                                     # list_type
//    | ident                                             # simple_type
//    ;

name_declaration:
    ident                                             # simple_type
    ;
    
type_declaration: ident ; 

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
    | '-' DIR? HIDE? COLOR? '-'
    | '.' DIR? COLOR? '.'
    | '-' DIR? COLOR? '->'
    | '<-' DIR? COLOR? '-'
    | '-' DIR? COLOR? '-*'
    | '*-' DIR? COLOR? '-'
    | '-' DIR? COLOR? '-o'
    | 'o-' DIR? COLOR? '-'
    | '<|-' DIR? COLOR? '-'       
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
    
//    | '*-right' HIDE? COLOR? '->'
//    | '*-left' HIDE? COLOR? '->'
//    | '*-left' HIDE? COLOR? '->'
//    | '-right' HIDE COLOR? '-|>'
    
    | '*' DIR? HIDE? COLOR? '->'
    | '-' DIR? HIDE? COLOR? '-|>'
    
    | '.' DIR? COLOR? '.>' 
    
    | '<.' DIR? COLOR? '.' 
    
    ;

	


color:
	'<' 'color:' IDENT '>'
	;
	

line:
     ~( DISCRIMINATOR | ENDLEGEND | RPAR | LBRAC | NEWLINE )+ 
     ;
        

legend_line:
     ~( '|' | NEWLINE | DISCRIMINATOR | ENDLEGEND)+ 
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

// NEWLINE  :  ENDLINE ; // [\r\n]+; // '\r'? '\n'; // [\r\n];

IDENT : IDENT1 | (IDENT1 '.' IDENT1)+ ;
IDENT1 : NONDIGIT ( DIGIT | NONDIGIT )*;
NUMBER : ( DIGIT )+;
HEX : '#' ( DIGIT | NONDIGIT )+;

QSTR : '"' ~'"'+ '"' ;

value : (IDENT | NUMBER | HEX);

COMMENT :
    ('\'' .*? NEWLINE)  -> channel(HIDDEN)
    ;
    
WS  : [ ]+ -> channel(HIDDEN); // toss out whitespace
   
NEWLINE : ENDLINE ;

//=========================================================
// Fragments
//=========================================================
fragment NONDIGIT : [_a-zA-Z#@-];
fragment DIGIT :  [0-9];
fragment UNSIGNED_INTEGER : DIGIT+;
fragment ENDLINE  :  [\r\n]+; 

