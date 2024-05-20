import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:live_activity_android_flutter/live_activity_manager.dart';

import 'model/live_activity_model.dart';

void main() {
  runApp(MyApp());
}
 const platform = MethodChannel('com.example.method_channel_example/channel');

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: LiveActivityPage(title: 'Flutter Demo Home Page'),
    );
  }
}

class LiveActivityPage extends StatefulWidget {
  LiveActivityPage({required this.title});

  final String title;

  @override
  _LiveActivityPageState createState() => _LiveActivityPageState();
}

class _LiveActivityPageState extends State<LiveActivityPage> {
  int seconds = 0;
  bool isRunning = false;
  Timer? timer;

  /// channel key is used to send data from flutter to swift side over
  /// a unique bridge (link between flutter & kotlin)
  final LiveActivityManager diManager = LiveActivityManager(channelKey: 'DI');


  void startTimer() {
    setState(() {
      isRunning = true;
    });

    // invoking startLiveActivity Method
    diManager.startLiveActivity(
      jsonData: LiveActivityDataModel(elapsedSeconds: 0).toMap(),
    );

    timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        seconds++;
      });

      // invoking the updateLiveActivity Method
      diManager.updateLiveActivity(
        jsonData: LiveActivityDataModel(
          elapsedSeconds: seconds,
        ).toMap(),
      );
    });
  }

  void stopTimer() {
    timer?.cancel();
    setState(() {
      seconds = 0;
      isRunning = false;
    });

    // invoking the stopLiveActivity Method
    diManager.stopLiveActivity();
  }

  @override
  void dispose() {
    timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("LIVE ACTIVITY"),
      ),
      body: Center(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            ElevatedButton(
                child: Text("CHECK PERMISSION"),
                onPressed: diManager.checkLayoutPermission
            ),
            const SizedBox(height: 50),
            Text(
              'Stopwatch: $seconds seconds',
              style: const TextStyle(fontSize: 24),
            ),
            const SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                ElevatedButton(
                    child: Text("START"),
                    onPressed: isRunning ? null : startTimer
                ),
                SizedBox(width: 40),
                ElevatedButton(
                  child: Text("STOP"),
                  onPressed: isRunning ? stopTimer : null,
                ),
              ],
            )
          ],
        )
      )
    );
  }
}
