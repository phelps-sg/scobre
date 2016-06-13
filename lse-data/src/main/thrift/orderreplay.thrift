/*
 * (c) Steve Phelps 2014
 */

/*
 *  bool        Boolean, one byte
 *  byte        Signed byte
 *  i16         Signed 16-bit integer
 *  i32         Signed 32-bit integer
 *  i64         Signed 64-bit integer
 *  double      64-bit floating point value
 *  string      String
 *  binary      Blob (byte array)
 *  map<t1,t2>  Map from one type to another
 *  list<t1>    Ordered list of one type
 *  set<t1>     Set of unique elements of one type
 *
 */

namespace java org.ccfea.tickdata.thrift
namespace py orderreplay

/**
 * Order-book replay service
 */
service OrderReplay {

   /**
    * Replay tick events
    **/
   map<string, list<double>> replay(1:string assetId, 2:list<string> variables,
                                                        3:i64 startDateTime, 4:i64 endDateTime),

   map<string, list<double>> shuffledReplay(1:string assetId, 2:list<string> variables,
                                                        3:double proportionShuffling, 4:i32 windowSize,
                                                        5:bool intraWindow, 6:i32 offsetting, 7:i32 attribute)

   map<string, list<double>> shuffledReplayDateRange(1:string assetId, 2:list<string> variables,
                                                        3:double proportionShuffling, 4:i32 windowSize,
                                                        5:bool intraWindow, 6:i32 offsetting, 7:i32 attribute,
                                                        8:i64 startDateTime, 9:i64 endDateTime)
}
