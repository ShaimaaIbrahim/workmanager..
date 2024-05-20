class LiveActivityDataModel {
  final int elapsedSeconds;

  LiveActivityDataModel({
    required this.elapsedSeconds,
  });

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'elapsedSeconds': elapsedSeconds,
    };
  }
}