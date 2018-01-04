/*
 * This package includes cryptographic code written by Paul Tero(http://www.tero.co.uk/des/),
 * Michael Hayworth(http://www.netdealing.com) and Eric Young(eay@cryptsoft.com).
 *
 */
function des(h,Q,V,R,G,U){var N=new Array(16843776,0,65536,16843780,16842756,66564,4,65536,1024,16843776,16843780,1024,16778244,16842756,16777216,4,1028,16778240,16778240,66560,66560,16842752,16842752,16778244,65540,16777220,16777220,65540,0,1028,66564,16777216,65536,16843780,4,16842752,16843776,16777216,16777216,1024,16842756,65536,66560,16777220,1024,4,16778244,66564,16843780,65540,16842752,16778244,16777220,1028,66564,16843776,1028,16778240,16778240,0,65540,66560,0,16842756);var M=new Array(-2146402272,-2147450880,32768,1081376,1048576,32,-2146435040,-2147450848,-2147483616,-2146402272,-2146402304,-2147483648,-2147450880,1048576,32,-2146435040,1081344,1048608,-2147450848,0,-2147483648,32768,1081376,-2146435072,1048608,-2147483616,0,1081344,32800,-2146402304,-2146435072,32800,0,1081376,-2146435040,1048576,-2147450848,-2146435072,-2146402304,32768,-2146435072,-2147450880,32,-2146402272,1081376,32,32768,-2147483648,32800,-2146402304,1048576,-2147483616,1048608,-2147450848,-2147483616,1048608,1081344,0,-2147450880,32800,-2147483648,-2146435040,-2146402272,1081344);var L=new Array(520,134349312,0,134348808,134218240,0,131592,134218240,131080,134217736,134217736,131072,134349320,131080,134348800,520,134217728,8,134349312,512,131584,134348800,134348808,131592,134218248,131584,131072,134218248,8,134349320,512,134217728,134349312,134217728,131080,520,131072,134349312,134218240,0,512,131080,134349320,134218240,134217736,512,0,134348808,134218248,131072,134217728,134349320,8,131592,131584,134217736,134348800,134218248,520,134348800,131592,8,134348808,131584);
var K=new Array(8396801,8321,8321,128,8396928,8388737,8388609,8193,0,8396800,8396800,8396929,129,0,8388736,8388609,1,8192,8388608,8396801,128,8388608,8193,8320,8388737,1,8320,8388736,8192,8396928,8396929,129,8388736,8388609,8396800,8396929,129,0,0,8396800,8320,8388736,8388737,1,8396801,8321,8321,128,8396929,129,1,8192,8388609,8193,8396928,8388737,8193,8320,8388608,8396801,128,8388608,8192,8396928);
var J=new Array(256,34078976,34078720,1107296512,524288,256,1073741824,34078720,1074266368,524288,33554688,1074266368,1107296512,1107820544,524544,1073741824,33554432,1074266112,1074266112,0,1073742080,1107820800,1107820800,33554688,1107820544,1073742080,0,1107296256,34078976,33554432,1107296256,524544,524288,1107296512,256,33554432,1073741824,34078720,1107296512,1074266368,33554688,1073741824,1107820544,34078976,1074266368,256,33554432,1107820544,1107820800,524544,1107296256,1107820800,34078720,0,1074266112,1107296256,524544,33554688,1073742080,524288,0,1074266112,34078976,1073742080);var H=new Array(536870928,541065216,16384,541081616,541065216,16,541081616,4194304,536887296,4210704,4194304,536870928,4194320,536887296,536870912,16400,0,4194320,536887312,16384,4210688,536887312,16,541065232,541065232,0,4210704,541081600,16400,4210688,541081600,536870912,536887296,16,541065232,4210688,541081616,4194304,16400,536870928,4194304,536887296,536870912,16400,536870928,541081616,4210688,541065216,4210704,541081600,0,541065232,16,16384,541065216,4210704,16384,4194320,536887312,0,541081600,536870912,4194320,536887312);
var F=new Array(2097152,69206018,67110914,0,2048,67110914,2099202,69208064,69208066,2097152,0,67108866,2,67108864,69206018,2050,67110912,2099202,2097154,67110912,67108866,69206016,69208064,2097154,69206016,2048,2050,69208066,2099200,2,67108864,2099200,67108864,2099200,2097152,67110914,67110914,69206018,69206018,2,2097154,67108864,67110912,2097152,69208064,2050,2099202,69208064,2050,67108866,69208066,69206016,2099200,0,2,69208066,0,2099202,69206016,2048,67108866,67110912,2048,2097154);var D=new Array(268439616,4096,262144,268701760,268435456,268439616,64,268435456,262208,268697600,268701760,266240,268701696,266304,4096,64,268697600,268435520,268439552,4160,266240,262208,268697664,268701696,4160,0,0,268697664,268435520,268439552,266304,262144,266304,262144,268701696,4096,64,268697664,4096,266304,268439552,64,268435520,268697600,268697664,268435456,262144,268439616,0,268701760,262208,268435520,268697600,268439552,268439616,0,268701760,266240,266240,4160,4160,262208,268435456,268701696);
var T=des_createKeys(h);var X=0,c,a,e,g,C,B,E,f,W;var O,Z,A,I;var S,Y;var d=Q.length;var P=0;var b=T.length==32?3:9;if(b==3){W=V?new Array(0,32,2):new Array(30,-2,-2)}else{W=V?new Array(0,32,2,62,30,-2,64,96,2):new Array(94,62,-2,32,64,2,30,-2,-2)}if(U==2){Q+="        "}else{if(U==1){e=8-(d%8);Q+=String.fromCharCode(e,e,e,e,e,e,e,e);if(e==8){d+=8}}else{if(!U){Q+="\0\0\0\0\0\0\0\0"}}}result="";tempresult="";if(R==1){O=(G.charCodeAt(X++)<<24)|(G.charCodeAt(X++)<<16)|(G.charCodeAt(X++)<<8)|G.charCodeAt(X++);A=(G.charCodeAt(X++)<<24)|(G.charCodeAt(X++)<<16)|(G.charCodeAt(X++)<<8)|G.charCodeAt(X++);X=0}while(X<d){E=(Q.charCodeAt(X++)<<24)|(Q.charCodeAt(X++)<<16)|(Q.charCodeAt(X++)<<8)|Q.charCodeAt(X++);f=(Q.charCodeAt(X++)<<24)|(Q.charCodeAt(X++)<<16)|(Q.charCodeAt(X++)<<8)|Q.charCodeAt(X++);if(R==1){if(V){E^=O;f^=A}else{Z=O;I=A;O=E;A=f}}e=((E>>>4)^f)&252645135;f^=e;E^=(e<<4);e=((E>>>16)^f)&65535;f^=e;E^=(e<<16);e=((f>>>2)^E)&858993459;E^=e;f^=(e<<2);e=((f>>>8)^E)&16711935;E^=e;f^=(e<<8);e=((E>>>1)^f)&1431655765;f^=e;E^=(e<<1);E=((E<<1)|(E>>>31));f=((f<<1)|(f>>>31));for(a=0;a<b;a+=3){S=W[a+1];Y=W[a+2];for(c=W[a];c!=S;c+=Y){C=f^T[c];
B=((f>>>4)|(f<<28))^T[c+1];e=E;E=f;f=e^(M[(C>>>24)&63]|K[(C>>>16)&63]|H[(C>>>8)&63]|D[C&63]|N[(B>>>24)&63]|L[(B>>>16)&63]|J[(B>>>8)&63]|F[B&63])}e=E;E=f;f=e}E=((E>>>1)|(E<<31));f=((f>>>1)|(f<<31));e=((E>>>1)^f)&1431655765;f^=e;E^=(e<<1);e=((f>>>8)^E)&16711935;E^=e;f^=(e<<8);e=((f>>>2)^E)&858993459;E^=e;f^=(e<<2);e=((E>>>16)^f)&65535;f^=e;E^=(e<<16);e=((E>>>4)^f)&252645135;f^=e;E^=(e<<4);if(R==1){if(V){O=E;A=f}else{E^=Z;f^=I}}tempresult+=String.fromCharCode((E>>>24),((E>>>16)&255),((E>>>8)&255),(E&255),(f>>>24),((f>>>16)&255),((f>>>8)&255),(f&255));P+=8;if(P==512){result+=tempresult;tempresult="";P=0}}return result+tempresult}
function des_createKeys(H){pc2bytes0=new Array(0,4,536870912,536870916,65536,65540,536936448,536936452,512,516,536871424,536871428,66048,66052,536936960,536936964);pc2bytes1=new Array(0,1,1048576,1048577,67108864,67108865,68157440,68157441,256,257,1048832,1048833,67109120,67109121,68157696,68157697);pc2bytes2=new Array(0,8,2048,2056,16777216,16777224,16779264,16779272,0,8,2048,2056,16777216,16777224,16779264,16779272);pc2bytes3=new Array(0,2097152,134217728,136314880,8192,2105344,134225920,136323072,131072,2228224,134348800,136445952,139264,2236416,134356992,136454144);pc2bytes4=new Array(0,262144,16,262160,0,262144,16,262160,4096,266240,4112,266256,4096,266240,4112,266256);pc2bytes5=new Array(0,1024,32,1056,0,1024,32,1056,33554432,33555456,33554464,33555488,33554432,33555456,33554464,33555488);pc2bytes6=new Array(0,268435456,524288,268959744,2,268435458,524290,268959746,0,268435456,524288,268959744,2,268435458,524290,268959746);pc2bytes7=new Array(0,65536,2048,67584,536870912,536936448,536872960,536938496,131072,196608,133120,198656,537001984,537067520,537004032,537069568);pc2bytes8=new Array(0,262144,0,262144,2,262146,2,262146,33554432,33816576,33554432,33816576,33554434,33816578,33554434,33816578);
pc2bytes9=new Array(0,268435456,8,268435464,0,268435456,8,268435464,1024,268436480,1032,268436488,1024,268436480,1032,268436488);pc2bytes10=new Array(0,32,0,32,1048576,1048608,1048576,1048608,8192,8224,8192,8224,1056768,1056800,1056768,1056800);pc2bytes11=new Array(0,16777216,512,16777728,2097152,18874368,2097664,18874880,67108864,83886080,67109376,83886592,69206016,85983232,69206528,85983744);pc2bytes12=new Array(0,4096,134217728,134221824,524288,528384,134742016,134746112,16,4112,134217744,134221840,524304,528400,134742032,134746128);pc2bytes13=new Array(0,4,256,260,0,4,256,260,1,5,257,261,1,5,257,261);
var C=H.length>8?3:1;var J=new Array(32*C);var G=new Array(0,0,1,1,1,1,1,1,0,1,1,1,1,1,1,0);var F,A,D=0,B=0,I;for(var E=0;E<C;E++){left=(H.charCodeAt(D++)<<24)|(H.charCodeAt(D++)<<16)|(H.charCodeAt(D++)<<8)|H.charCodeAt(D++);right=(H.charCodeAt(D++)<<24)|(H.charCodeAt(D++)<<16)|(H.charCodeAt(D++)<<8)|H.charCodeAt(D++);I=((left>>>4)^right)&252645135;right^=I;left^=(I<<4);I=((right>>>-16)^left)&65535;left^=I;right^=(I<<-16);I=((left>>>2)^right)&858993459;right^=I;left^=(I<<2);I=((right>>>-16)^left)&65535;left^=I;right^=(I<<-16);I=((left>>>1)^right)&1431655765;right^=I;left^=(I<<1);I=((right>>>8)^left)&16711935;left^=I;right^=(I<<8);I=((left>>>1)^right)&1431655765;right^=I;left^=(I<<1);I=(left<<8)|((right>>>20)&240);left=(right<<24)|((right<<8)&16711680)|((right>>>8)&65280)|((right>>>24)&240);right=I;for(i=0;i<G.length;i++){if(G[i]){left=(left<<2)|(left>>>26);right=(right<<2)|(right>>>26)}else{left=(left<<1)|(left>>>27);right=(right<<1)|(right>>>27)}left&=-15;right&=-15;F=pc2bytes0[left>>>28]|pc2bytes1[(left>>>24)&15]|pc2bytes2[(left>>>20)&15]|pc2bytes3[(left>>>16)&15]|pc2bytes4[(left>>>12)&15]|pc2bytes5[(left>>>8)&15]|pc2bytes6[(left>>>4)&15];A=pc2bytes7[right>>>28]|pc2bytes8[(right>>>24)&15]|pc2bytes9[(right>>>20)&15]|pc2bytes10[(right>>>16)&15]|pc2bytes11[(right>>>12)&15]|pc2bytes12[(right>>>8)&15]|pc2bytes13[(right>>>4)&15];I=((A>>>16)^F)&65535;J[B++]=F^I;J[B++]=A^(I<<16)}}return J}
function stringToHex(C){var D="";var B=new Array("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f");for(var A=0;A<C.length;A++){D+=B[C.charCodeAt(A)>>4]+B[C.charCodeAt(A)&15]}return D};