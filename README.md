
---

# 🎥 VODSIE HLS Video Processing Pipeline

## 📚 Project Overview

In the modern world, delivering video content efficiently is critical for online platforms. The aim of this project was to **build an automated, serverless pipeline** that:
- **Converts uploaded videos into HLS format** (which supports adaptive streaming)
- **Stores the outputs in S3**
- **Serves the content globally via CloudFront for smooth, scalable playback**

The **goal** was to gain hands-on experience in designing an event-driven video processing architecture using **AWS services** like Lambda, MediaConvert, S3, and CloudFront.

---

## 🎯 What We Learned

During this project, we deepened our knowledge in the following areas:
- How to configure **S3 event triggers** to automatically invoke a **Lambda function**.
- How to dynamically **create MediaConvert jobs** programmatically via Boto3 inside Lambda.
- How **CloudFront** dramatically improves video streaming performance by caching content closer to users.
- How to manage **IAM roles and permissions** for MediaConvert and Lambda.
- How to architect **serverless, scalable, and cost-efficient** video processing workflows.

---

# 📦 Project Documentation

## 🛠️ Technologies Used
- AWS Lambda (Python 3.10)
- AWS Elemental MediaConvert
- AWS S3
- AWS IAM Roles
- AWS CloudFront
- Git / GitHub
- Java / Springboot
- MySQL

---

## 📦 Project Structure

```
/vodsie-hls-processor
├── lambda/
│   └── lambda_function.py  # Main Lambda function script
├── README.md                # Project documentation
└── docs/                    # (Optional future expansion)
```

---

## ⚙️ How It Works

1. **Upload a video** to the S3 bucket `vodsie/videos/`.
2. **Lambda function** is automatically triggered by the S3 event.
3. Lambda **creates a MediaConvert job** to:
   - Convert the uploaded video into HLS format.
   - Save output files under `vodsie/hls/converted/`.
4. **CloudFront CDN** serves the HLS output globally with low latency.

---

## 🌍 Why Use AWS CloudFront?

- **Global Caching**: Videos are cached at edge locations closer to users, reducing buffering.
- **High Availability**: Ensures users get a smooth streaming experience even under high traffic.
- **Security**: Integrates easily with signed URLs for secure video distribution.
- **Performance**: Improves video startup times and stream quality for viewers across the world.

---

## ⚡ Role of the Lambda Function

- **Detects** when a new video is uploaded to S3 (`vodsie/videos/`).
- **Submits** a job to AWS MediaConvert to transform the video into HLS format.
- **Automates** the workflow — no human intervention needed after upload.
- **Handles** MediaConvert endpoint discovery dynamically for high reliability.

---

## 🚀 What Can Be Done Next?

- **Multi-Resolution Streaming**: Output 1080p, 720p, 480p HLS renditions automatically.
- **Thumbnail Generation**: Generate video thumbnails with another MediaConvert or Lambda step.
- **Signed URLs**: Protect video content with CloudFront signed URLs or signed cookies.
- **Upload Interface**: Create a React/Next.js frontend for users to upload videos easily.
- **Notifications**: Add SNS notifications to alert when video conversion is complete.
- **Auto Cleanup**: Delete original large uploads from `vodsie/videos/` after processing.

---

## 🧠 System Architecture

```
+----------------+         +------------------+         +-------------------+
| Upload Video   |  --->    | S3 Trigger Event  |  --->   | Lambda Function    |
| to S3 Bucket   |          | (Object Created)  |         | Creates MediaConvert|
| vodsie/videos/ |          |                  |         | Job                |
+----------------+          +------------------+         +-------------------+
                                                            |
                                                            v
                                                   +-------------------+
                                                   | HLS Output in S3   |
                                                   | vodsie/hls/converted|
                                                   +-------------------+
                                                            |
                                                            v
                                                   +-------------------+
                                                   | CloudFront CDN     |
                                                   | (Smooth Playback)  |
                                                   +-------------------+
```

---

## 🚀 Deployment Steps

### 1. Setup IAM Role
- Create a role `MediaConvertServiceRole` with permissions for:
  - MediaConvert full access
  - S3 read/write access
- Attach this role to the Lambda function.

### 2. Setup Lambda Function
- Runtime: **Python 3.10**
- Handler: `lambda_function.lambda_handler`
- Trigger: S3 event (Object Created) from bucket `vodsie/videos/`

### 3. Configure MediaConvert
- No special setup; the Lambda handles endpoint discovery automatically.

### 4. CloudFront Setup
- Create a CloudFront distribution with an origin pointing to your S3 bucket.
- Update bucket permissions to allow CloudFront access.

---

## 📄 Important Environment Variables

| Variable | Description |
|:---|:---|
| `MEDIA_CONVERT_ROLE` | ARN of the MediaConvert IAM Role |
| `S3_BUCKET` | Source/Target bucket name (`bucketName`) |
| `OUTPUT_PREFIX` | Output path inside the bucket (`hls/converted/`) |
| `REGION` | AWS Region (`eu-west-1`) |

---

## 📦 Example MediaConvert Job Submission

- Input: `s3://vodsie/videos/uploaded_video.mp4`
- Output: `s3://vodsie/hls/converted/full_video.m3u8`
- Public URL: `https://your-cloudfront-domain.net/hls/converted/full_video.m3u8`

---

## 👨‍💻 Author

- **Sammy Ocharo Obanyi**  
- [LinkedIn Profile](www.linkedin.com/in/sammy-ocharo-82943a1a1)  
- AWS Cloud Engineer | Backend Developer | Video Streaming Enthusiast

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).

---
